package com.hirehack.hirehack.strategy;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.factory.AuthResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * OTP-based authentication strategy implementation.
 * Handles authentication using One-Time Password verification.
 */
@Component
@RequiredArgsConstructor
public class OtpAuthenticationStrategy implements AuthenticationStrategy {

    private final AuthResponseFactory authResponseFactory;

    @Override
    public AuthResponseDto authenticate(User user, Object credentials) {
        // For OTP authentication, we assume the OTP has already been verified
        // in the controller layer, so we just generate the auth response
        return authResponseFactory.createAuthResponse(user);
    }

    @Override
    public String getStrategyType() {
        return "OTP";
    }
}
