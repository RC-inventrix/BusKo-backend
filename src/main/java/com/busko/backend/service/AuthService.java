package com.busko.backend.service;

import com.busko.backend.entity.User;
import com.busko.backend.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Placeholder authentication service for the MVP phase.
 *
 * <p><b>SECURITY WARNING:</b> This service performs a plain-text password comparison.
 * It must <em>never</em> be used in production. Passwords must be hashed with a
 * strong algorithm (e.g. BCrypt via {@code PasswordEncoder}) before being stored,
 * and compared using a constant-time equals to prevent timing attacks.
 * Full JWT-based authentication with BCrypt password hashing will replace this
 * in the next development phase.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to authenticate a user by username and password.
     *
     * @param username the username to look up
     * @param password the raw password to verify
     * @return the matching {@link User} wrapped in an {@link Optional}, or
     *         {@link Optional#empty()} if authentication fails
     */
    public Optional<User> login(String username, String password) {
        // TODO: Replace plain-text comparison with BCryptPasswordEncoder
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }
}
