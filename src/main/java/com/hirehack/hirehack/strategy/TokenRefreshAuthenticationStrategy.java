package com.hirehack.hirehack.strategy;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.factory.AuthResponseFactory;
import com.hirehack.hirehack.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Token refresh authentication strategy implementation.
 * Handles authentication using refresh token validation.
 */
@Component
@RequiredArgsConstructor
public class TokenRefreshAuthenticationStrategy implements AuthenticationStrategy {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthResponseFactory authResponseFactory;

    @Override
    public AuthResponseDto authenticate(User user, Object credentials) {
        String refreshToken = (String) credentials;
        
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = jwtUtil.extractUserId(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
        
        if (!jwtUtil.validateToken(refreshToken, userId)) {
            throw new RuntimeException("Invalid refresh token");
        }

        User authenticatedUser = (User) userDetails;
        return authResponseFactory.createAuthResponse(authenticatedUser);
    }

    @Override
    public String getStrategyType() {
        return "TOKEN_REFRESH";
    }
}
