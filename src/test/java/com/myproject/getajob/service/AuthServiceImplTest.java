package com.myproject.getajob.service;

import com.myproject.getajob.dto.LoginDto;
import com.myproject.getajob.dto.LoginResponseDto;
import com.myproject.getajob.repository.RoleRepository;
import com.myproject.getajob.repository.UserRepository;
import com.myproject.getajob.repository.VerificationTokenRepository;
import com.myproject.getajob.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private RoleRepository roleRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private JwtTokenProvider jwtTokenProvider;

        @Mock
        private VerificationTokenRepository verificationTokenRepository;

        @Mock
        private EmailService emailService;

        @InjectMocks
        private AuthServiceImpl authService;

        @Test
        void should_login_successfully_when_credentials_are_valid() {
                // Arrange
                LoginDto loginDto = new LoginDto();
                loginDto.setEmail("test@example.com");
                loginDto.setPassword("password");

                Authentication authenticationResult = mock(Authentication.class);
                String expectedToken = "fake-jwt-token";

                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authenticationResult);
                when(jwtTokenProvider.generateToken(authenticationResult))
                                .thenReturn(expectedToken);

                // Act
                LoginResponseDto response = authService.login(loginDto);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getAccessToken()).isEqualTo(expectedToken);
                assertThat(response.getTokenType()).isEqualTo("Bearer");

                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                verify(jwtTokenProvider).generateToken(authenticationResult);
        }

        @Test
        void should_throw_exception_when_authentication_fails() {
                // Arrange
                LoginDto loginDto = new LoginDto();
                loginDto.setEmail("wrong@example.com");
                loginDto.setPassword("wrong");

                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new BadCredentialsException("Bad credentials"));

                // Act & Assert
                assertThatThrownBy(() -> authService.login(loginDto))
                                .isInstanceOf(BadCredentialsException.class)
                                .hasMessage("Bad credentials");

                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                verifyNoInteractions(jwtTokenProvider);
        }

        @Test
        void should_register_user_successfully_when_email_not_exists() {
                // Arrange
                com.myproject.getajob.dto.RegistrationDto registrationDto = new com.myproject.getajob.dto.RegistrationDto();
                registrationDto.setFirstName("John");
                registrationDto.setLastName("Doe");
                registrationDto.setEmail("john.doe@example.com");
                registrationDto.setPassword("password123");
                registrationDto.setPhone("1234567890");
                registrationDto.setCity("New York");

                // Mock dependencies
                when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(java.util.Optional.empty());
                when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");

                com.myproject.getajob.entity.Role roleCandidate = new com.myproject.getajob.entity.Role();
                roleCandidate.setName("ROLE_CANDIDATE");
                when(roleRepository.findByName("ROLE_CANDIDATE")).thenReturn(java.util.Optional.of(roleCandidate));

                com.myproject.getajob.entity.User savedUser = new com.myproject.getajob.entity.User();
                savedUser.setId(1L);
                savedUser.setEmail(registrationDto.getEmail());
                savedUser.setFirstName(registrationDto.getFirstName());
                when(userRepository.save(any(com.myproject.getajob.entity.User.class))).thenReturn(savedUser);

                // Act
                authService.registerUser(registrationDto);

                // Assert
                org.mockito.ArgumentCaptor<com.myproject.getajob.entity.User> userCaptor = org.mockito.ArgumentCaptor
                                .forClass(com.myproject.getajob.entity.User.class);
                verify(userRepository).save(userCaptor.capture());
                com.myproject.getajob.entity.User capturedUser = userCaptor.getValue();

                assertThat(capturedUser.getEmail()).isEqualTo(registrationDto.getEmail());
                assertThat(capturedUser.getFirstName()).isEqualTo(registrationDto.getFirstName());
                assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
                assertThat(capturedUser.getRoles()).contains(roleCandidate);

                verify(verificationTokenRepository).save(any(com.myproject.getajob.entity.VerificationToken.class));
                verify(emailService).sendVerificationEmail(eq(savedUser.getEmail()), anyString());
        }

        @Test
        void should_throw_exception_when_email_already_exists() {
                // Arrange
                com.myproject.getajob.dto.RegistrationDto registrationDto = new com.myproject.getajob.dto.RegistrationDto();
                registrationDto.setEmail("existing@example.com");

                when(userRepository.findByEmail(registrationDto.getEmail()))
                                .thenReturn(java.util.Optional.of(new com.myproject.getajob.entity.User()));

                // Act & Assert
                assertThatThrownBy(() -> authService.registerUser(registrationDto))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessage("Email already exists!");

                verify(userRepository, never()).save(any(com.myproject.getajob.entity.User.class));
                verify(verificationTokenRepository, never())
                                .save(any(com.myproject.getajob.entity.VerificationToken.class));
                verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
        }

        @Test
        void should_create_default_role_when_role_does_not_exist() {
                // Arrange
                com.myproject.getajob.dto.RegistrationDto registrationDto = new com.myproject.getajob.dto.RegistrationDto();
                registrationDto.setEmail("newuser@example.com");
                registrationDto.setPassword("password");

                when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(java.util.Optional.empty());
                when(passwordEncoder.encode(anyString())).thenReturn("encoded");
                when(roleRepository.findByName("ROLE_CANDIDATE")).thenReturn(java.util.Optional.empty());

                com.myproject.getajob.entity.Role newRole = new com.myproject.getajob.entity.Role();
                newRole.setName("ROLE_CANDIDATE");
                when(roleRepository.save(any(com.myproject.getajob.entity.Role.class))).thenReturn(newRole);

                com.myproject.getajob.entity.User savedUser = new com.myproject.getajob.entity.User();
                savedUser.setEmail(registrationDto.getEmail());
                when(userRepository.save(any(com.myproject.getajob.entity.User.class))).thenReturn(savedUser);

                // Act
                authService.registerUser(registrationDto);

                // Assert
                verify(roleRepository).save(any(com.myproject.getajob.entity.Role.class));

                org.mockito.ArgumentCaptor<com.myproject.getajob.entity.User> userCaptor = org.mockito.ArgumentCaptor
                                .forClass(com.myproject.getajob.entity.User.class);
                verify(userRepository).save(userCaptor.capture());

                assertThat(userCaptor.getValue().getRoles()).extracting("name").contains("ROLE_CANDIDATE");
        }
}
