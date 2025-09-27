package com.hirehack.hirehack.factory;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.util.JwtUtil;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating authentication response objects.
 * Implements the Factory pattern to encapsulate auth response creation logic.
 */
@Component
public class AuthResponseFactory {

    private final JwtUtil jwtUtil;

    public AuthResponseFactory(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates an authentication response for a user with generated tokens.
     *
     * @param user the authenticated user
     * @return an AuthResponseDto with access token, refresh token, and user information
     */
    public AuthResponseDto createAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        return new AuthResponseDto(
                token,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    /**
     * Creates an authentication response with custom token type.
     *
     * @param user the authenticated user
     * @param tokenType the type of token (e.g., "Bearer", "JWT")
     * @return an AuthResponseDto with custom token type
     */
    public AuthResponseDto createAuthResponse(User user, String tokenType) {
        String token = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        return new AuthResponseDto(
                token,
                refreshToken,
                tokenType,
                user.getId(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}
