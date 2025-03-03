package com.UniRide.Backend.service;

import com.UniRide.Backend.model.User;
import com.UniRide.Backend.model.Otp;
import com.UniRide.Backend.repo.UserRepository;
import com.UniRide.Backend.repo.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.UniRide.Backend.constants.constants;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRepository otpRepository;

    /**
     * Handles user login/registration using phone number and OTP.
     * If the user is new, prompts for additional details after OTP verification.
     *
     * @param phoneNo The phone number of the user.
     * @param otpCode The OTP code for verification.
     * @param userDetails Optional user details (name, email) for first-time registration.
     * @return The logged-in/registered user.
     * @throws IllegalArgumentException If the OTP is invalid or the user is not found.
     */
    public User loginOrRegisterUser(String phoneNo, String otpCode, User userDetails) {
        // Check if the user already exists
        Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);

        if (userOptional.isPresent()) {
            // Existing user: Verify OTP and log in
            User user = userOptional.get();
            verifyOtp(phoneNo, otpCode); // Throws exception if OTP is invalid
            return user;
        } else {
            // New user: Verify OTP and register with additional details
            verifyOtp(phoneNo, otpCode); // Throws exception if OTP is invalid

            if (userDetails == null || userDetails.getName() == null || userDetails.getEmail() == null) {
                throw new IllegalArgumentException(constants.ERROR_NAME_EMAIL_REQUIRED);
            }

            // Create a new user
            User newUser = new User();
            newUser.setPhoneNo(phoneNo);
            newUser.setName(userDetails.getName());
            newUser.setEmail(userDetails.getEmail());
            newUser.setEnabled(true); // User is enabled after OTP verification

            // Save the new user to the database
            return userRepository.save(newUser);
        }
    }

    public Optional<User> getUserByPhoneNo(String phoneNo) {
        return userRepository.findByPhoneNo(phoneNo);
    }

    /**
     * Verifies the OTP for a user.
     *
     * @param phoneNo The phone number of the user.
     * @param otpCode The OTP code to verify.
     * @throws IllegalArgumentException If the OTP is invalid or expired.
     */
    private void verifyOtp(String phoneNo, String otpCode) {
        // Find the user by phone number
        Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);

        User user = userOptional.orElse(null); // Allow null for new users

        // Find the latest OTP for the user (or phone number if user is not yet registered)
        Optional<Otp> otpOptional = user != null
                ? otpRepository.findTopByUserOrderByCreatedAtDesc(user)
                : otpRepository.findTopByPhoneNoOrderByCreatedAtDesc(phoneNo);

        if (otpOptional.isEmpty()) {
            throw new IllegalArgumentException(constants.ERROR_NO_OTP_FOUND);
        }

        Otp otp = otpOptional.get();

        // Check if the OTP is valid and not expired
        if (!otp.getOtpCode().equals(otpCode)) {
            throw new IllegalArgumentException(constants.ERROR_INVALID_OTP);
        }
        if (otp.isExpired()) {
            throw new IllegalArgumentException(constants.ERROR_OTP_EXPIRED);
        }
    }

    /**
     * Sends an OTP to the given phone number.
     *
     * @param phoneNo The phone number to send the OTP to.
     */
    public void sendOtp(String phoneNo) {
        // Check if the user already exists
        Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);

        if (userOptional.isPresent()) {
            // Existing user: Send OTP for login
            User user = userOptional.get();
            otpService.sendOtp(user.getPhoneNo(), user.getId());
        } else {
            // New user: Send OTP for registration
            otpService.sendOtp(phoneNo, null); // No user ID for new users
        }
    }
}