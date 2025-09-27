package com.hirehack.hirehack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long userId;
    private String phoneNumber;
    private String fullName;
    private String role;
}

