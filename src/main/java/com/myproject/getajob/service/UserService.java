package com.myproject.getajob.service;

import com.myproject.getajob.entity.User;

public interface UserService {

    User registerUser(User user);

    User findByEmail(String email);

    User updateProfile(String email, com.myproject.getajob.dto.UpdateProfileDto updateProfileDto);

    void deleteProfileImage(String email);

    // Admin Methods
    java.util.List<com.myproject.getajob.dto.UserDto> getAllUsers();

    void deleteUser(Long id);

    com.myproject.getajob.dto.UserDto updateUserRole(Long id, String roleName);
}
