package com.hirehack.hirehack.repository;

import com.hirehack.hirehack.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    
    @Query("SELECT o FROM OtpVerification o WHERE o.phoneNumber = :phoneNumber AND o.isVerified = false AND o.expiresAt > :currentTime ORDER BY o.createdAt DESC")
    Optional<OtpVerification> findLatestUnverifiedOtpByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("currentTime") OffsetDateTime currentTime);
    
    @Query("SELECT o FROM OtpVerification o WHERE o.phoneNumber = :phoneNumber AND o.otpCode = :otpCode AND o.isVerified = false AND o.expiresAt > :currentTime")
    Optional<OtpVerification> findValidOtpByPhoneNumberAndCode(@Param("phoneNumber") String phoneNumber, @Param("otpCode") String otpCode, @Param("currentTime") OffsetDateTime currentTime);
    
    void deleteByPhoneNumberAndIsVerifiedTrue(String phoneNumber);
    
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") OffsetDateTime currentTime);
}

