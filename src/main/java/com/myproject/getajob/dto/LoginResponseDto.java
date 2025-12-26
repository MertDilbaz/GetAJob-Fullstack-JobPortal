package com.myproject.getajob.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    public String accessToken;
    public String tokenType = "Bearer";
}
