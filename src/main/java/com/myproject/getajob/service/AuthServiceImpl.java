package com.myproject.getajob.service;

import com.myproject.getajob.dto.LoginDto;
import com.myproject.getajob.dto.LoginResponseDto;
import com.myproject.getajob.dto.RegistrationDto;
import com.myproject.getajob.dto.UserProfileDto;
import com.myproject.getajob.entity.Role;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.entity.VerificationToken;
import com.myproject.getajob.repository.RoleRepository;
import com.myproject.getajob.repository.UserRepository;
import com.myproject.getajob.repository.VerificationTokenRepository;
import com.myproject.getajob.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            VerificationTokenRepository tokenRepository,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void registerUser(RegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false); // Disabled until verified

        // Extended Profile Fields
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getCity());
        user.setBio(dto.getBio());
        user.setJobTitle(dto.getJobTitle());
        user.setEducation(dto.getEducation());
        user.setGithub(dto.getGithubUrl());
        user.setSex(dto.getSex());
        user.setWebsite(dto.getLinkedinUrl()); // Mapping LinkedIn to Website as per previous logic logic
        user.setLinkedin(dto.getLinkedinUrl()); // Also setting linkedin if available from previous steps

        user.setSkills(dto.getSkills());

        // Default role: ROLE_CANDIDATE
        String roleName = "ROLE_CANDIDATE";
        Role userRole = roleRepository.findByName(roleName).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roleRepository.save(newRole);
        });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Generate and Save Verification Token
        VerificationToken token = new VerificationToken(savedUser);
        tokenRepository.save(token);

        // Send Verification Email
        emailService.sendVerificationEmail(savedUser.getEmail(), token.getToken());
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return new LoginResponseDto(token, "Bearer");
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        String roleName = "Bilinmiyor";
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roleName = user.getRoles().iterator().next().getName();
        }

        UserProfileDto dto = new UserProfileDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roleName);
        dto.setBio(user.getBio());
        dto.setSkills(user.getSkills());
        dto.setImageUrl(user.getImageUrl());
        dto.setJobTitle(user.getJobTitle());
        dto.setLocation(user.getLocation());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setEducation(user.getEducation());
        dto.setWebsite(user.getWebsite());
        dto.setLinkedin(user.getLinkedin());
        dto.setGithub(user.getGithub());

        return dto;
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new com.myproject.getajob.exception.ResourceNotFoundException(
                        "Invalid verification token"));

        java.util.Calendar cal = java.util.Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }
}