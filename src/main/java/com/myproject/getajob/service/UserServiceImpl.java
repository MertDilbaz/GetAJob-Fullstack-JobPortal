package com.myproject.getajob.service;

import com.myproject.getajob.entity.Role;
import com.myproject.getajob.repository.RoleRepository;
import com.myproject.getajob.repository.UserRepository;
import com.myproject.getajob.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @SuppressWarnings("null")
    public User updateProfile(String email, com.myproject.getajob.dto.UpdateProfileDto updateProfileDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateProfileDto.getBio() != null) {
            user.setBio(updateProfileDto.getBio());
        }
        if (updateProfileDto.getSkills() != null) {
            user.setSkills(updateProfileDto.getSkills());
        }
        if (updateProfileDto.getImageUrl() != null) {
            user.setImageUrl(updateProfileDto.getImageUrl());
        }

        // Update new fields
        if (updateProfileDto.getJobTitle() != null)
            user.setJobTitle(updateProfileDto.getJobTitle());
        if (updateProfileDto.getLocation() != null)
            user.setLocation(updateProfileDto.getLocation());
        if (updateProfileDto.getPhone() != null)
            user.setPhone(updateProfileDto.getPhone());
        if (updateProfileDto.getAddress() != null)
            user.setAddress(updateProfileDto.getAddress());
        if (updateProfileDto.getEducation() != null)
            user.setEducation(updateProfileDto.getEducation());
        if (updateProfileDto.getWebsite() != null)
            user.setWebsite(updateProfileDto.getWebsite());
        if (updateProfileDto.getLinkedin() != null)
            user.setLinkedin(updateProfileDto.getLinkedin());
        if (updateProfileDto.getGithub() != null)
            user.setGithub(updateProfileDto.getGithub());

        return userRepository.save(user);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteProfileImage(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setImageUrl(null);
        userRepository.save(user);
    }

    @Override
    public java.util.List<com.myproject.getajob.dto.UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            com.myproject.getajob.dto.UserDto dto = new com.myproject.getajob.dto.UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setEnabled(user.isEnabled());
            // Safe role display
            String roles = user.getRoles().stream().map(Role::getName).reduce((a, b) -> a + ", " + b).orElse("N/A");
            dto.setRole(roles);
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @SuppressWarnings("null")
    public com.myproject.getajob.dto.UserDto updateUserRole(Long id, String roleName) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        // For simplicity, we are replacing all roles or adding?
        // Usually "change role" implies replacing.
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User saved = userRepository.save(user);

        com.myproject.getajob.dto.UserDto dto = new com.myproject.getajob.dto.UserDto();
        dto.setId(saved.getId());
        dto.setEmail(saved.getEmail());
        dto.setRole(role.getName());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setEnabled(saved.isEnabled());
        return dto;
    }
}
// CompanyService ve JoblistingService eklenecek
