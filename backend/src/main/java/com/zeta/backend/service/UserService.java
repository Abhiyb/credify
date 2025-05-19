package com.zeta.backend.service;

import com.zeta.backend.exception.DuplicateUserException;
import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.User;
import com.zeta.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        // Other validations if needed
        return userRepository.save(user);
    }

    public User loginUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
