package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.AdminStatsDto;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> verifyAdmin(Authentication authentication) {
        return ResponseEntity.ok(Map.of("message", "Admin verified successfully"));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsDto> getStats(Authentication authentication) {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/interviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Interview>> getAllInterviews(Authentication authentication) {
        return ResponseEntity.ok(adminService.getAllInterviews());
    }

}