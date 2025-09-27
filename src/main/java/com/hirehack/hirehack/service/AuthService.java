package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.dto.LoginRequestDto;
import com.hirehack.hirehack.dto.RegisterRequestDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.factory.AuthResponseFactory;
import com.hirehack.hirehack.factory.UserFactory;
import com.hirehack.hirehack.observer.EventPublisher;
import com.hirehack.hirehack.observer.UserRegistrationEvent;
import com.hirehack.hirehack.repository.UserRepository;
import com.hirehack.hirehack.service.interfaces.AuthServiceInterface;
import com.hirehack.hirehack.strategy.AuthenticationContext;
import com.hirehack.hirehack.util.PhoneNumberUtil;
import com.hirehack.hirehack.util.JwtUtil;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthServiceInterface {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserFactory userFactory;
    private final AuthResponseFactory authResponseFactory;
    private final AuthenticationContext authenticationContext;
    private final EventPublisher eventPublisher;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(registerRequest.getPhoneNumber());
        
        if (userRepository.findByPhoneNumber(normalizedPhoneNumber).isPresent()) {
            throw new RuntimeException("User already exists with phone number: " + normalizedPhoneNumber);
        }

        User user = userFactory.createUserFromRegistration(registerRequest);
        User savedUser = userRepository.save(user);
        
        // Publish user registration event
        eventPublisher.publishEvent(new UserRegistrationEvent(savedUser, Map.of(
                "registrationMethod", "OTP",
                "timestamp", savedUser.getCreatedAt()
        )));
        
        return authResponseFactory.createAuthResponse(savedUser);
    }

    public AuthResponseDto login(LoginRequestDto loginRequest) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(loginRequest.getPhoneNumber());
        
        // Check if user exists
        User user = userRepository.findByPhoneNumber(normalizedPhoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone number: " + normalizedPhoneNumber));

        // Use OTP authentication strategy
        return authenticationContext.authenticate("OTP", user, null);
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        Long userId = jwtUtil.extractUserId(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
        User user = (User) userDetails;
        
        // Use token refresh authentication strategy
        return authenticationContext.authenticate("TOKEN_REFRESH", user, refreshToken);
    }

}
