package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.UserDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.service.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * REST controller for user operations.
 * Handles user profile management, resume upload, and admin operations.
 * Uses dependency injection with interfaces for better testability and maintainability.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceInterface userService;

    @PostMapping("/update-profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody UserDto userDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Updating profile for user: {}", currentUser.getId());
        User user = userService.updateUserProfile(currentUser.getId(), userDto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Getting profile for user: {}", currentUser.getId());
        User user = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/check/{phoneNumber}")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(@PathVariable String phoneNumber) {
        log.info("Checking if user exists with phone number: {}", phoneNumber);
        boolean exists = userService.doesUserExist(phoneNumber);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<Map<String, String>> uploadResume(@RequestParam("file") MultipartFile file, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Uploading resume for user: {}", currentUser.getId());
        
        try {
            userService.storeResume(currentUser.getId(), file);
            return ResponseEntity.ok(Map.of("message", "Resume uploaded successfully."));
        } catch (IOException e) {
            log.error("Failed to process resume for user: {}", currentUser.getId(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Failed to process resume."));
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Admin requesting all users - page: {}, size: {}", page, size);
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }
}