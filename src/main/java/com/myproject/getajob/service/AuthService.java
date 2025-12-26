package com.myproject.getajob.service;

import com.myproject.getajob.dto.LoginDto;
import com.myproject.getajob.dto.LoginResponseDto;
import com.myproject.getajob.dto.RegistrationDto;
import com.myproject.getajob.dto.UserProfileDto; // YENİ EKLENDİ

public interface AuthService {
    void registerUser(RegistrationDto dto);

    LoginResponseDto login(LoginDto loginDto);

    // Profil bilgisini getiren metot tanımı
    UserProfileDto getMyProfile(String email);

    void verifyEmail(String token);
}