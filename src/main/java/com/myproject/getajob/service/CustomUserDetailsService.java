package com.myproject.getajob.service;

import com.myproject.getajob.entity.Role;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService; // Implemented interface
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("CustomUserDetailsService: Attempting to load user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("CustomUserDetailsService: User not found: " + email);
                    return new UsernameNotFoundException("User not found by email: " + email);
                });
        System.out.println("CustomUserDetailsService: Found user: " + user.getEmail() + ", Enabled: " + user.isEnabled()
                + ", Roles: " + (user.getRoles() != null ? user.getRoles().size() : "null"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        if (roles == null) {
            return java.util.Collections.emptyList();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

    }

}
