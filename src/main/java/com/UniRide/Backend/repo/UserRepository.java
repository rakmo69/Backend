package com.UniRide.Backend.repo;

import com.UniRide.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Find a user by phone number
    Optional<User> findByPhoneNo(String phoneNo);

    // Check if a user exists by email
    boolean existsByEmail(String email);

    // Check if a user exists by phone number
    boolean existsByPhoneNo(String phoneNo);
}