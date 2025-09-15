package com.example.demo.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hash the plain text password (used during registration)
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    // Compare raw password with hashed password (used during login)
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
