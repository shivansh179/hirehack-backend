package com.hirehack.hirehack.dto;

import lombok.Data;

@Data
public class UserDto {
    private String phoneNumber;
    private String fullName;
    private String profession;
    private Integer yearsOfExperience;
}