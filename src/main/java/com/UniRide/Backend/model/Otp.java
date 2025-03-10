package com.UniRide.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class) // Enable auditing
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6) // OTP codes are typically 6 digits
    private String otpCode;

    @CreatedDate // Automatically sets the creation timestamp
    @Column(nullable = false, updatable = false) // Prevent updates after creation
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    @Column(nullable = false, unique = true, length = 15)
    private String phoneNo;

    @ManyToOne(fetch = FetchType.LAZY) // Use LAZY fetching for better performance
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20) // OTP type (e.g., "LOGIN", "PASSWORD_RESET")
    private String otpType;

    // Parameterized constructor for easy creation
    public Otp(String otpCode, LocalDateTime expiresAt, User user, String otpType) {
        this.otpCode = otpCode;
        this.expiresAt = expiresAt;
        this.user = user;
        this.otpType = otpType;
    }

    // Helper method to check if the OTP is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "Otp{" +
                "id=" + id +
                ", otpCode='" + otpCode + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", otpType=" + otpType + '\'' +
                '}';
    }
}