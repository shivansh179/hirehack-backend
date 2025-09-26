package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.AdminStatsDto;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.UserRepository;
import com.hirehack.hirehack.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    // A simple security check method
    private void checkAdmin(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRole() != User.UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAdmin(@RequestBody Map<String, String> payload) {
        String phoneNumber = payload.get("phoneNumber");
        checkAdmin(phoneNumber);
        return ResponseEntity.ok(Map.of("message", "Admin verified successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats(@RequestHeader("X-Admin-Phone-Number") String phoneNumber) {
        checkAdmin(phoneNumber);
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("X-Admin-Phone-Number") String phoneNumber) {
        checkAdmin(phoneNumber);
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/interviews")
    public ResponseEntity<List<Interview>> getAllInterviews(@RequestHeader("X-Admin-Phone-Number") String phoneNumber) {
        checkAdmin(phoneNumber);
        return ResponseEntity.ok(adminService.getAllInterviews());
    }

}