package com.hirehack.hirehack.service.interfaces;

import com.hirehack.hirehack.dto.UserDto;
import com.hirehack.hirehack.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for user service operations.
 * Provides abstraction for user management, profile updates, and data retrieval.
 */
public interface UserServiceInterface {
    
    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user's unique identifier
     * @return the user entity
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    User getUserById(Long id);
    
    /**
     * Updates a user's profile with the provided information.
     *
     * @param userId the user's unique identifier
     * @param userDto the user data transfer object containing updated information
     * @return the updated user entity
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    User updateUserProfile(Long userId, UserDto userDto);
    
    /**
     * Checks if a user exists with the given phone number.
     *
     * @param phoneNumber the phone number to check
     * @return true if user exists, false otherwise
     */
    boolean doesUserExist(String phoneNumber);
    
    /**
     * Stores a resume file for a user by extracting text content.
     *
     * @param userId the user's unique identifier
     * @param file the resume file to process and store
     * @throws IOException if file processing fails
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    void storeResume(Long userId, MultipartFile file) throws IOException;
    
    /**
     * Retrieves all users with pagination support.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a map containing paginated user data and metadata
     */
    Map<String, Object> getAllUsers(int page, int size);
}
