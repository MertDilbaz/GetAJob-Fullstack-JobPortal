package com.myproject.getajob.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String bio;
    private List<String> skills;
    private String imageUrl;
    private Boolean resumeUpload;

    private String jobTitle;
    private String location;
    private String phone;
    private String address;
    private String sex;
    private String education;
    private String website;
    private String linkedin;
    private String github;

    public UserProfileDto(String email, String firstName, String lastName, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}