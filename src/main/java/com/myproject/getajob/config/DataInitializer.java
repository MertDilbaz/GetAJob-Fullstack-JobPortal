package com.myproject.getajob.config;

import com.myproject.getajob.entity.Role;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.repository.RoleRepository;
import com.myproject.getajob.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("DEBUG: DataInitializer STARTED");
            try {
                // Init Roles
                if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                    roleRepository.save(new Role("ROLE_USER"));
                }
                if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                    roleRepository.save(new Role("ROLE_ADMIN"));
                }
                if (roleRepository.findByName("ROLE_EMPLOYER").isEmpty()) {
                    roleRepository.save(new Role("ROLE_EMPLOYER"));
                }

                // Init Admin User
                User admin = userRepository.findByEmail("admin@getajob.com").orElse(new User());
                if (admin.getId() == null) {
                    admin.setFirstName("Admin");
                    admin.setLastName("User");
                    admin.setEmail("admin@getajob.com");
                    admin.setPhone("1234567890");
                }
                admin.setPassword(passwordEncoder.encode("admin123"));
                Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
                userRepository.save(admin);
                System.out.println("Admin user updated/created: admin@getajob.com / admin123");

                // Init Secondary Admin
                User secondAdmin = userRepository.findByEmail("mertdilbaz202@gmail.com").orElse(new User());
                if (secondAdmin.getId() == null) {
                    secondAdmin.setFirstName("Mert");
                    secondAdmin.setLastName("Dilbaz");
                    secondAdmin.setEmail("mertdilbaz202@gmail.com");
                    secondAdmin.setPhone("5555555555");
                }
                secondAdmin.setPassword(passwordEncoder.encode("123Mert123"));
                secondAdmin.setEnabled(true);
                Set<Role> secondAdminRoles = new HashSet<>();
                secondAdminRoles.add(adminRole);
                secondAdmin.setRoles(secondAdminRoles);
                userRepository.save(secondAdmin);
                System.out.println("Secondary admin user updated/created: mertdilbaz202@gmail.com / admin123");

                // Deleted hardcoded test user (etalhaaydinn) to prevent it from reappearing
                // after deletion

            } catch (Exception e) {
                System.err.println("ERROR initializing data: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
