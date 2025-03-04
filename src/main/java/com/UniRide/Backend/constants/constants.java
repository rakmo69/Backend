package com.UniRide.Backend.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class constants {

    //otp related
    public static final int OTP_LENGTH = 6; // OTP length
    public static final int OTP_EXPIRY_MINUTES = 3; // OTP expiry time in minutes

    //twilo related constants
    public static final String TWILIO_ACCOUNT_SID = "your_account_sid";
    public static final String TWILIO_AUTH_TOKEN = "your_auth_token";
    public static final String TWILIO_PHONE_NUMBER = "twilo no."; // Replace with Your Twilio phone number

    //error messages
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_INVALID_OTP = "Invalid OTP";
    public static final String ERROR_EXPIRED_OTP = "OTP has expired";
    public static final String ERROR_INVALID_PASSWORD = "Invalid password.";
    public static final String ERROR_NO_OTP_FOUND = "No OTP found for the user.";
    public static final String ERROR_OTP_EXPIRED = "OTP has expired.";
    public static final String ERROR_NAME_EMAIL_REQUIRED = "Name and email are required for first-time registration.";


}
