package com.hirehack.hirehack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponseDto {
    private String message;
    private String otpId; // For tracking OTP verification
    private boolean userExists;
    private UserDto userDetails; // Only if user exists
    private String token; // JWT token if user exists and is signed in
    private String refreshToken; // Refresh token if user exists and is signed in
    private String type = "Bearer"; // Token type
    private Long userId; // User ID if user exists and is signed in
    private String role; // User role if user exists and is signed in
}

