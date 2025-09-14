package com.hirehack.hirehack.dto;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long totalInterviews;
    private long completedInterviews;
}