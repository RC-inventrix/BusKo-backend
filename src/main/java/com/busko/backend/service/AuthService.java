package com.busko.backend.service;

import com.busko.backend.entity.Role;
import com.busko.backend.entity.User;
import com.busko.backend.repository.UserRepository;
import com.busko.backend.security.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates a new anonymous user in the database and generates a JWT for them.
     */
    public Map<String, String> initAnonymousUser(Role role) {
        // 1. Create the frictionless user
        User user = new User();
        user.setRole(role);
        user = userRepository.save(user);

        // 2. Generate the silent JWT token using their new UUID
        String token = jwtUtil.generateToken(user.getId());

        // 3. Return both to the mobile app
        Map<String, String> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("token", token);

        return response;
    }
}