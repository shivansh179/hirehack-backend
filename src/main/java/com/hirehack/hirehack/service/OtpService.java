package com.hirehack.hirehack.service;

import com.hirehack.hirehack.entity.OtpVerification;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.OtpVerificationRepository;
import com.hirehack.hirehack.repository.UserRepository;
import com.hirehack.hirehack.service.interfaces.NotificationServiceInterface;
import com.hirehack.hirehack.service.interfaces.OtpServiceInterface;
import com.hirehack.hirehack.util.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService implements OtpServiceInterface {

    private final OtpVerificationRepository otpVerificationRepository;
    private final UserRepository userRepository;
    private final NotificationServiceInterface notificationService;
    private final Random random = new Random();

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    @Transactional
    public OtpVerification generateAndSendOtp(String phoneNumber) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        
        // Clean up any existing unverified OTPs for this phone number
        cleanupUnverifiedOtps(normalizedPhoneNumber);
        
        // Generate OTP
        String otpCode = generateOtpCode();
        
        // Create OTP verification record
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setPhoneNumber(normalizedPhoneNumber);
        otpVerification.setOtpCode(otpCode);
        otpVerification.setExpiresAt(OffsetDateTime.now().plusMinutes(otpExpirationMinutes));
        
        OtpVerification savedOtp = otpVerificationRepository.save(otpVerification);
        
        // Send OTP via SMS
        try {
            String message = String.format("Your HireHack OTP is: %s. Valid for %d minutes.", otpCode, otpExpirationMinutes);
            log.error(message);
//            notificationService.sendSms(normalizedPhoneNumber, message);
        } catch (Exception e) {
            log.error("Failed to send OTP SMS to {}", normalizedPhoneNumber, e);
            // Continue execution even if SMS fails - OTP is still generated
        }
        
        log.info("OTP generated and sent to phone number: {}", normalizedPhoneNumber);
        return savedOtp;
    }

    @Transactional(readOnly = true)
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        OffsetDateTime currentTime = OffsetDateTime.now();
        
        Optional<OtpVerification> otpOptional = otpVerificationRepository
                .findValidOtpByPhoneNumberAndCode(normalizedPhoneNumber, otpCode, currentTime);
        
        if (otpOptional.isEmpty()) {
            log.warn("Invalid or expired OTP for phone number: {}", normalizedPhoneNumber);
            return false;
        }
        
        OtpVerification otpVerification = otpOptional.get();
        
        // Check if max attempts exceeded
        if (otpVerification.getAttempts() >= otpVerification.getMaxAttempts()) {
            log.warn("Max OTP attempts exceeded for phone number: {}", normalizedPhoneNumber);
            return false;
        }
        
        // Increment attempts
        otpVerification.setAttempts(otpVerification.getAttempts() + 1);
        
        if (otpCode.equals(otpVerification.getOtpCode())) {
            // OTP is correct
            otpVerification.setVerified(true);
            otpVerificationRepository.save(otpVerification);
            log.info("OTP verified successfully for phone number: {}", normalizedPhoneNumber);
            return true;
        } else {
            // OTP is incorrect
            otpVerificationRepository.save(otpVerification);
            log.warn("Incorrect OTP entered for phone number: {}", normalizedPhoneNumber);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public boolean isOtpVerified(String phoneNumber) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        OffsetDateTime currentTime = OffsetDateTime.now();
        
        Optional<OtpVerification> otpOptional = otpVerificationRepository
                .findLatestUnverifiedOtpByPhoneNumber(normalizedPhoneNumber, currentTime);
        
        return otpOptional.isPresent() && otpOptional.get().isVerified();
    }

    @Transactional(readOnly = true)
    public boolean userExists(String phoneNumber) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        return userRepository.findByPhoneNumber(normalizedPhoneNumber).isPresent();
    }

    @Transactional(readOnly = true)
    public User getUserByPhoneNumber(String phoneNumber) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        return userRepository.findByPhoneNumber(normalizedPhoneNumber)
                .orElse(null);
    }

    @Transactional
    public void cleanupVerifiedOtps(String phoneNumber) {
        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        otpVerificationRepository.deleteByPhoneNumberAndIsVerifiedTrue(normalizedPhoneNumber);
    }

    @Transactional
    public void cleanupExpiredOtps() {
        otpVerificationRepository.deleteExpiredOtps(OffsetDateTime.now());
    }

    private void cleanupUnverifiedOtps(String phoneNumber) {
        // Delete any existing unverified OTPs for this phone number
        otpVerificationRepository.findAll().stream()
                .filter(otp -> otp.getPhoneNumber().equals(phoneNumber) && !otp.isVerified())
                .forEach(otp -> otpVerificationRepository.delete(otp));
    }

    private String generateOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

}

