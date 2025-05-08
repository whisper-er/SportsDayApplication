package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.util.DynamoDbUtil;
import com.example.demo.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {
    private final DynamoDbUtil dynamoDbUtil;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public UserService(DynamoDbUtil dynamoDbUtil, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.dynamoDbUtil = dynamoDbUtil;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (user.getEmailId() == null || !EMAIL_PATTERN.matcher(user.getEmailId()).matches()) {
            return "Invalid email format.";
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        User getUser = dynamoDbUtil.getUserByUsername(user.getUsername());
        if(getUser != null){
            return "Username already taken";
        }
        // Hash the password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Save the user
        dynamoDbUtil.saveUser(user);
        return "User registered successfully.";
    }

    public String login(String username, String password) {
        User user = dynamoDbUtil.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found!"); // User not found
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(username); // Generate and return JWT
        }
        throw new RuntimeException("Invalid username or password");
    }
}