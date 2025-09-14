package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.UserDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerOrUpdateUser(@RequestBody UserDto userDto) {
        User user = userService.registerOrUpdateUser(userDto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/check/{phoneNumber}")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(@PathVariable String phoneNumber) {
        boolean exists = userService.doesUserExist(phoneNumber);
        // Returns a simple JSON like {"exists": true}
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<Map<String, String>> uploadResume(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("file") MultipartFile file) {
        try {
            userService.storeResume(phoneNumber, file);
            return ResponseEntity.ok(Map.of("message", "Resume uploaded successfully."));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to process resume."));
        }
    }
}