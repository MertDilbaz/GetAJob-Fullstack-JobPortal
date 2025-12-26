package com.myproject.getajob.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class UpdateProfileDto {
    private String bio;
    private List<String> skills;
    private String imageUrl;

    // New fields
    private String jobTitle;
    private String location;
    private String phone;
    private String address;
    private String education;
    private String website;
    private String linkedin;
    private String github;
}
