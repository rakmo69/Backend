package com.UniRide.Backend.repo;

import com.UniRide.Backend.model.Otp;
import com.UniRide.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface OtpRepository extends JpaRepository<Otp, Long> {

    // Delete expired OTPs
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);

    // Find the latest OTP for a user
    Optional<Otp> findTopByUserOrderByCreatedAtDesc(User user);

    // Find the latest OTP by phone number
    Optional<Otp> findTopByPhoneNoOrderByCreatedAtDesc(String phoneNo);

}
