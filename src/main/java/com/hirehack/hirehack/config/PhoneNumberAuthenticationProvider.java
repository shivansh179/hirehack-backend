package com.hirehack.hirehack.config;

import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.UserRepository;
import com.hirehack.hirehack.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhoneNumberAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        // For phone number authentication, we don't validate passwords
        // We just verify the user exists by phone number and return user ID as username
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
            
            return new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
            );
        } catch (UsernameNotFoundException e) {
            // Try to find user by phone number for login
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
            
            return new UsernamePasswordAuthenticationToken(
                    user, 
                    null, 
                    user.getAuthorities()
            );
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
