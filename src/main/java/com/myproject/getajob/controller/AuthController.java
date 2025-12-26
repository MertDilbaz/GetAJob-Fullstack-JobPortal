package com.myproject.getajob.controller;

import com.myproject.getajob.dto.LoginDto;
import com.myproject.getajob.dto.LoginResponseDto;
import com.myproject.getajob.dto.RegistrationDto;
import com.myproject.getajob.dto.UserProfileDto; // YENİ
import com.myproject.getajob.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // YENİ
import org.springframework.security.core.context.SecurityContextHolder; // YENİ
import org.springframework.web.bind.annotation.*; // GetMapping için

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationDto dto) {
        authService.registerUser(dto);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody LoginDto loginDto) {
        LoginResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    // Giriş yapmış kullanıcının bilgilerini döner
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile() {
        // Şu anki kullanıcının emailini SecurityContext'ten al
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        // Servise git ve bilgileri getir
        UserProfileDto profile = authService.getMyProfile(currentEmail);

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully! You can now login.");
    }
}