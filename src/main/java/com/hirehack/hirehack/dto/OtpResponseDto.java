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
}

