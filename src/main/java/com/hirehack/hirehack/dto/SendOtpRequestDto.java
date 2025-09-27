package com.hirehack.hirehack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequestDto {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}

