package com.hirehack.hirehack.strategy;

import com.hirehack.hirehack.dto.AuthResponseDto;
import com.hirehack.hirehack.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Context class for authentication strategy pattern.
 * Manages different authentication strategies and delegates authentication to the appropriate strategy.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationContext {

    private final List<AuthenticationStrategy> authenticationStrategies;
    private Map<String, AuthenticationStrategy> strategyMap;

    /**
     * Initializes the strategy map after dependency injection.
     */
    private void initializeStrategyMap() {
        if (strategyMap == null) {
            strategyMap = authenticationStrategies.stream()
                    .collect(Collectors.toMap(
                            AuthenticationStrategy::getStrategyType,
                            Function.identity()
                    ));
        }
    }

    /**
     * Authenticates a user using the specified strategy.
     *
     * @param strategyType the type of authentication strategy to use
     * @param user the user to authenticate
     * @param credentials additional credentials if needed
     * @return authentication response with tokens
     * @throws RuntimeException if strategy not found or authentication fails
     */
    public AuthResponseDto authenticate(String strategyType, User user, Object credentials) {
        initializeStrategyMap();
        
        AuthenticationStrategy strategy = strategyMap.get(strategyType);
        if (strategy == null) {
            throw new RuntimeException("Authentication strategy not found: " + strategyType);
        }
        
        return strategy.authenticate(user, credentials);
    }

    /**
     * Gets all available authentication strategy types.
     *
     * @return list of available strategy types
     */
    public List<String> getAvailableStrategies() {
        initializeStrategyMap();
        return authenticationStrategies.stream()
                .map(AuthenticationStrategy::getStrategyType)
                .collect(Collectors.toList());
    }
}
