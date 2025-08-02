package com.cinema.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordTestController {

    @GetMapping("/test/hash")
    public String generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password123");
        return "BCrypt hash for 'password123': " + hash;
    }

    @GetMapping("/test/verify")
    public String verifyHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String currentHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        boolean matches = encoder.matches("password123", currentHash);
        return "Current hash matches 'password123': " + matches;
    }
}