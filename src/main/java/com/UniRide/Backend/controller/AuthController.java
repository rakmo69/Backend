package com.UniRide.Backend.controller;

import com.UniRide.Backend.model.User;
import com.UniRide.Backend.service.UserService;
import com.UniRide.Backend.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    // Endpoint to send OTP for login/registration
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String phoneNo) {
        try {
            userService.sendOtp(phoneNo);
            return ResponseEntity.ok("OTP sent successfully to " + phoneNo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send OTP: " + e.getMessage());
        }
    }

    // Endpoint to verify OTP and login/register user
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String phoneNo,
            @RequestParam String otpCode,
            @RequestBody(required = false) User userDetails) {
        try {
            User user = userService.loginOrRegisterUser(phoneNo, otpCode, userDetails);
            return ResponseEntity.ok("User verified successfully: " + user.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("OTP verification failed: " + e.getMessage());
        }
    }

    // Endpoint to resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String phoneNo) {
        try {
            userService.sendOtp(phoneNo);
            return ResponseEntity.ok("OTP resent successfully to " + phoneNo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to resend OTP: " + e.getMessage());
        }
    }

    // Endpoint to get user details by phone number
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam String phoneNo) {
        try {
            Optional<User> user = userService.getUserByPhoneNo(phoneNo);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch user: " + e.getMessage());
        }
    }
}