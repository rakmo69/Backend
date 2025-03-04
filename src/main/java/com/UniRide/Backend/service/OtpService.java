package com.UniRide.Backend.service;

import com.UniRide.Backend.constants.constants;
import com.UniRide.Backend.model.Otp;
import com.UniRide.Backend.model.User;
import com.UniRide.Backend.repo.OtpRepository;
import com.UniRide.Backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Sends an OTP to the specified phone number and saves it in the database.
     *
     * @param phoneNumber The phone number to send the OTP to.
     * @param userId      The ID of the user requesting the OTP.
     */
    public void sendOtp(String phoneNumber, Long userId) {
        // Generate OTP
        String otpCode = generateOtp();

        // Log the OTP (for testing purposes)
        System.out.println("OTP for " + phoneNumber + ": " + otpCode);

        // Save OTP in the database
        saveOtp(otpCode, userId);
    }

    /**
     * Generates a random OTP code.
     *
     * @return The generated OTP code.
     */
    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < constants.OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return otp.toString();
    }

    /**
     * Saves the OTP in the database.
     *
     * @param otpCode The OTP code to save.
     * @param userId  The ID of the user requesting the OTP.
     */
    private void saveOtp(String otpCode, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(constants.ERROR_USER_NOT_FOUND));

        // Use the parameterized constructor
        Otp otp = new Otp(otpCode, LocalDateTime.now().plusMinutes(constants.OTP_EXPIRY_MINUTES), user, "LOGIN");

        otpRepository.save(otp);
    }

    /**
     * Verifies the OTP for a user.
     *
     * @param userId  The ID of the user.
     * @param otpCode The OTP code to verify.
     * @return True if the OTP is valid, false otherwise.
     */
    public boolean verifyOtp(Long userId, String otpCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(constants.ERROR_USER_NOT_FOUND));

        // Find the latest OTP for the user
        Optional<Otp> otpOptional = otpRepository.findTopByUserOrderByCreatedAtDesc(user);

        if (otpOptional.isEmpty()) {
            throw new RuntimeException("No OTP found for the user");
        }

        Otp otp = otpOptional.get();

        // Check if the OTP is valid and not expired
        if (!otp.getOtpCode().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        return true;
    }

    /**
     * Cleans up expired OTPs from the database.
     * This method is scheduled to run every hour.
     */
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 milliseconds)
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpRepository.deleteExpiredOtps(now);
        System.out.println("Expired OTPs cleaned up at: " + now);
    }
}