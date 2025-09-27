package com.hirehack.hirehack.service.interfaces;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.dto.LoginRequestDto;
import com.hirehack.hirehack.dto.RegisterRequestDto;

/**
 * Interface for authentication service operations.
 * Provides abstraction for user authentication, registration, and token management.
 */
public interface AuthServiceInterface {
    
    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerRequest the registration request containing user details
     * @return authentication response with tokens and user information
     * @throws RuntimeException if user already exists or registration fails
     */
    AuthResponseDto register(RegisterRequestDto registerRequest);
    
    /**
     * Authenticates a user and generates authentication tokens.
     *
     * @param loginRequest the login request containing user credentials
     * @return authentication response with tokens and user information
     * @throws RuntimeException if user not found or authentication fails
     */
    AuthResponseDto login(LoginRequestDto loginRequest);
    
    /**
     * Refreshes authentication tokens using a valid refresh token.
     *
     * @param refreshToken the refresh token to validate and use for new token generation
     * @return new authentication response with refreshed tokens
     * @throws RuntimeException if refresh token is invalid or expired
     */
    AuthResponseDto refreshToken(String refreshToken);
}
