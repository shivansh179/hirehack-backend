package com.hirehack.hirehack.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for interview creation requests.
 * Uses Builder pattern for flexible object creation and validation annotations for data integrity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequestDto {
    
    @NotNull(message = "Interview duration is required")
    @Min(value = 5, message = "Interview duration must be at least 5 minutes")
    @Max(value = 180, message = "Interview duration must not exceed 180 minutes")
    private Integer interviewDurationMinutes;
    
    @NotBlank(message = "Role is required")
    @Size(min = 2, max = 100, message = "Role must be between 2 and 100 characters")
    private String role;
    
    @NotBlank(message = "Skills are required")
    @Size(min = 2, max = 500, message = "Skills must be between 2 and 500 characters")
    private String skills;
    
    @NotBlank(message = "Interview type is required")
    @Size(min = 2, max = 50, message = "Interview type must be between 2 and 50 characters")
    private String interviewType;
}