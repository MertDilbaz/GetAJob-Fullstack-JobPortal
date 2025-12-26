package com.myproject.getajob.controller;

import com.myproject.getajob.dto.UpdateProfileDto;
import com.myproject.getajob.entity.User;
import com.myproject.getajob.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileDto updateProfileDto, Principal principal) {
        String email = principal.getName();
        User updatedUser = userService.updateProfile(email, updateProfileDto);
        return ResponseEntity.ok(updatedUser);
    }

    // Granular Endpoints using the same DTO for simplicity (Partial updates
    // supported by service)
    @PutMapping("/profile/contact")
    public ResponseEntity<User> updateContact(@RequestBody UpdateProfileDto dto, Principal principal) {
        return updateProfile(dto, principal);
    }

    @PutMapping("/profile/identity")
    public ResponseEntity<User> updateIdentity(@RequestBody UpdateProfileDto dto, Principal principal) {
        return updateProfile(dto, principal);
    }

    @PutMapping("/profile/skills")
    public ResponseEntity<User> updateSkills(@RequestBody UpdateProfileDto dto, Principal principal) {
        return updateProfile(dto, principal);
    }

    @PutMapping("/profile/education")
    public ResponseEntity<User> updateEducation(@RequestBody UpdateProfileDto dto, Principal principal) {
        return updateProfile(dto, principal);
    }

    @PutMapping("/profile/socials")
    public ResponseEntity<User> updateSocials(@RequestBody UpdateProfileDto dto, Principal principal) {
        return updateProfile(dto, principal);
    }

    @DeleteMapping("/profile/image")
    public ResponseEntity<Void> deleteProfileImage(Principal principal) {
        userService.deleteProfileImage(principal.getName());
        return ResponseEntity.ok().build();
    }

    // Admin Endpoints
    @GetMapping("/admin")
    public ResponseEntity<java.util.List<com.myproject.getajob.dto.UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{id}/role")
    public ResponseEntity<com.myproject.getajob.dto.UserDto> updateUserRole(@PathVariable Long id,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }
}
