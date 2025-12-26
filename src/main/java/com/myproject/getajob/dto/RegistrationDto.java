package com.myproject.getajob.dto;

import lombok.Data;

@Data
public class RegistrationDto {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String userType;
    private String sex;
    // Removing userType selection as requested, strict implementation might keep it
    // as hardcoded or remove field.
    // User requested: "Role Logic: Remove the option...". I will remove it from DTO
    // or ignore it.

    // Extended Profile Fields
    private String phone;
    private String city; // Mapped to 'location' in Entity
    private String bio;
    private String jobTitle;
    private String education;

    // Socials
    private String linkedinUrl;
    private String githubUrl;

    // Skills (Using comma separated string for simple registration form input, or
    // List if JSON)
    private java.util.List<String> skills;
}
