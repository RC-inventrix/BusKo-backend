package com.busko.backend.repository;

import com.busko.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    // No custom methods needed. We only look up users by their UUID.
}