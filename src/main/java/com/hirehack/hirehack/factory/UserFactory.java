package com.hirehack.hirehack.factory;

import com.hirehack.hirehack.dto.RegisterRequestDto;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.util.PhoneNumberUtil;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating User entities.
 * Implements the Factory pattern to encapsulate user creation logic.
 */
@Component
public class UserFactory {

    /**
     * Creates a new User entity from registration request data.
     *
     * @param registerRequest the registration request containing user details
     * @return a new User entity with normalized phone number and default role
     */
    public User createUserFromRegistration(RegisterRequestDto registerRequest) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(registerRequest.getPhoneNumber());
        
        User user = new User();
        user.setPhoneNumber(normalizedPhoneNumber);
        user.setFullName(registerRequest.getFullName());
        user.setProfession(registerRequest.getProfession());
        user.setYearsOfExperience(registerRequest.getYearsOfExperience());
        user.setRole(User.UserRole.USER); // Default role
        
        return user;
    }

    /**
     * Creates a new User entity with minimal required information.
     *
     * @param phoneNumber the user's phone number
     * @param fullName the user's full name
     * @return a new User entity with basic information
     */
    public User createBasicUser(String phoneNumber, String fullName) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        
        User user = new User();
        user.setPhoneNumber(normalizedPhoneNumber);
        user.setFullName(fullName);
        user.setRole(User.UserRole.USER);
        
        return user;
    }

    /**
     * Creates a new admin User entity.
     *
     * @param phoneNumber the admin's phone number
     * @param fullName the admin's full name
     * @return a new User entity with admin role
     */
    public User createAdminUser(String phoneNumber, String fullName) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        
        User user = new User();
        user.setPhoneNumber(normalizedPhoneNumber);
        user.setFullName(fullName);
        user.setRole(User.UserRole.ADMIN);
        
        return user;
    }
}
