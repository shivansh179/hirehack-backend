package com.hirehack.hirehack.controller;

import com.hirehack.hirehack.dto.*;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.service.interfaces.AuthServiceInterface;
import com.hirehack.hirehack.service.interfaces.OtpServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, OTP verification, and token refresh.
 * Uses dependency injection with interfaces for better testability and maintainability.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServiceInterface authService;
    private final OtpServiceInterface otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody SendOtpRequestDto sendOtpRequest) {
        log.info("Sending OTP to phone number: {}", sendOtpRequest.getPhoneNumber());
        otpService.generateAndSendOtp(sendOtpRequest.getPhoneNumber());
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<OtpResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequest) {
        log.info("Verifying OTP for phone number: {}", verifyOtpRequest.getPhoneNumber());
        
        boolean isValidOtp = otpService.verifyOtp(verifyOtpRequest.getPhoneNumber(), verifyOtpRequest.getOtp());
        
        if (!isValidOtp) {
            return ResponseEntity.badRequest()
                    .body(new OtpResponseDto("Invalid or expired OTP", null, false, null));
        }
        
        boolean userExists = otpService.userExists(verifyOtpRequest.getPhoneNumber());
        
        if (userExists) {
            // User exists - return user details for login
            User user = otpService.getUserByPhoneNumber(verifyOtpRequest.getPhoneNumber());
            UserDto userDto = UserDto.builder()
                    .phoneNumber(user.getPhoneNumber())
                    .fullName(user.getFullName())
                    .profession(user.getProfession())
                    .yearsOfExperience(user.getYearsOfExperience())
                    .build();
            
            return ResponseEntity.ok(new OtpResponseDto(
                    "OTP verified successfully. User exists.",
                    "otp_verified",
                    true,
                    userDto
            ));
        } else {
            // User doesn't exist - return registration required
            return ResponseEntity.ok(new OtpResponseDto(
                    "OTP verified successfully. Registration required.",
                    "otp_verified",
                    false,
                    null
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        log.info("Registering new user with phone number: {}", registerRequest.getPhoneNumber());
        
        // Verify OTP before allowing registration
        if (!otpService.isOtpVerified(registerRequest.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(null); // Return error response
        }
        
        AuthResponseDto response = authService.register(registerRequest);
        
        // Clean up verified OTP after successful registration
        otpService.cleanupVerifiedOtps(registerRequest.getPhoneNumber());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Logging in user with phone number: {}", loginRequest.getPhoneNumber());
        
        // Verify OTP before allowing login
        if (!otpService.isOtpVerified(loginRequest.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(null); // Return error response
        }
        
        AuthResponseDto response = authService.login(loginRequest);
        
        // Clean up verified OTP after successful login
        otpService.cleanupVerifiedOtps(loginRequest.getPhoneNumber());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Refresh token is required");
        }
        
        log.info("Refreshing token");
        AuthResponseDto response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-phone/{phoneNumber}")
    public ResponseEntity<Map<String, Boolean>> validatePhoneNumber(@PathVariable String phoneNumber) {
        log.info("Validating phone number: {}", phoneNumber);
        try {
            com.hirehack.hirehack.util.PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}
