package com.hirehack.hirehack.dto;

import lombok.Data;

@Data
public class InterviewRequestDto {
    private String phoneNumber;
    private int interviewDurationMinutes;

    private String role;
    private String skills;
    private String interviewType;
}