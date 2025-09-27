package com.hirehack.hirehack.service.interfaces;

import com.hirehack.hirehack.entity.OtpVerification;
import com.hirehack.hirehack.entity.User;

/**
 * Interface for OTP (One-Time Password) service operations.
 * Provides abstraction for OTP generation, verification, and management.
 */
public interface OtpServiceInterface {
    
    /**
     * Generates and sends an OTP to the specified phone number.
     *
     * @param phoneNumber the phone number to send OTP to
     * @return the created OTP verification record
     */
    OtpVerification generateAndSendOtp(String phoneNumber);
    
    /**
     * Verifies an OTP code for a given phone number.
     *
     * @param phoneNumber the phone number associated with the OTP
     * @param otpCode the OTP code to verify
     * @return true if OTP is valid and verified, false otherwise
     */
    boolean verifyOtp(String phoneNumber, String otpCode);
    
    /**
     * Checks if an OTP has been verified for a given phone number.
     *
     * @param phoneNumber the phone number to check
     * @return true if OTP is verified, false otherwise
     */
    boolean isOtpVerified(String phoneNumber);
    
    /**
     * Checks if a user exists with the given phone number.
     *
     * @param phoneNumber the phone number to check
     * @return true if user exists, false otherwise
     */
    boolean userExists(String phoneNumber);
    
    /**
     * Retrieves a user by their phone number.
     *
     * @param phoneNumber the phone number to search for
     * @return the user entity if found, null otherwise
     */
    User getUserByPhoneNumber(String phoneNumber);
    
    /**
     * Cleans up verified OTPs for a given phone number.
     *
     * @param phoneNumber the phone number to clean up OTPs for
     */
    void cleanupVerifiedOtps(String phoneNumber);
    
    /**
     * Cleans up expired OTPs from the system.
     */
    void cleanupExpiredOtps();
}
