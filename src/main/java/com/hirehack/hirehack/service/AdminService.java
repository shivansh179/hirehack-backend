package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.AdminStatsDto;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.InterviewRepository;
import com.hirehack.hirehack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public AdminStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalInterviews = interviewRepository.count();
        long completedInterviews = interviewRepository.countByStatus("COMPLETED");
        return new AdminStatsDto(totalUsers, totalInterviews, completedInterviews);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Interview> getAllInterviews() {
        return interviewRepository.findAllByOrderByCreatedAtDesc();
    }
}