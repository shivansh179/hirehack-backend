package com.hirehack.hirehack.strategy;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.entity.User;

/**
 * Strategy interface for different authentication methods.
 * Implements the Strategy pattern to allow different authentication approaches.
 */
public interface AuthenticationStrategy {
    
    /**
     * Authenticates a user using the specific strategy implementation.
     *
     * @param user the user to authenticate
     * @param credentials additional credentials if needed
     * @return authentication response with tokens
     * @throws RuntimeException if authentication fails
     */
    AuthResponseDto authenticate(User user, Object credentials);
    
    /**
     * Gets the strategy type identifier.
     *
     * @return the strategy type name
     */
    String getStrategyType();
}
