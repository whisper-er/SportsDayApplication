package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.util.DynamoDbUtil;
import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private DynamoDbUtil dynamoDbUtil;

    @Mock
    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(dynamoDbUtil, jwtUtil, passwordEncoder);
    }

    @Test
    void testRegisterWithEmptyUsername() {
        User user = new User();
        user.setUsername("");
        user.setEmailId("test@example.com");
        user.setPassword("password123");

        String result = userService.register(user);
        assertEquals("Username cannot be empty.", result);
    }

    @Test
    void testRegisterWithNullUsername() {
        User user = new User();
        user.setUsername(null);
        user.setEmailId("test@example.com");
        user.setPassword("password123");

        String result = userService.register(user);
        assertEquals("Username cannot be empty.", result);
    }

    @Test
    void testRegisterWithInvalidEmail() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmailId("invalid-email");
        user.setPassword("password123");

        String result = userService.register(user);
        assertEquals("Invalid email format.", result);
    }

    @Test
    void testRegisterWithNullEmail() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmailId(null);
        user.setPassword("password123");

        String result = userService.register(user);
        assertEquals("Invalid email format.", result);
    }

    @Test
    void testRegisterWithShortPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmailId("test@example.com");
        user.setPassword("123");

        String result = userService.register(user);
        assertEquals("Password must be at least 6 characters long.", result);
    }

    @Test
    void testRegisterWithNullPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmailId("test@example.com");
        user.setPassword(null);

        String result = userService.register(user);
        assertEquals("Password must be at least 6 characters long.", result);
    }

    @Test
    void testRegisterUsernameAlreadyTaken() {
        User user = new User();
        user.setUsername("existingUser");
        user.setEmailId("test@example.com");
        user.setPassword("password123");

        when(dynamoDbUtil.getUserByUsername("existingUser")).thenReturn(new User());

        String result = userService.register(user);
        assertEquals("Username already taken", result);
    }

    @Test
    void testSuccessfulUserRegistration() {
        User user = new User();
        user.setUsername("newUser");
        user.setEmailId("test@example.com");
        user.setPassword("password123");

        when(dynamoDbUtil.getUserByUsername("newUser")).thenReturn(null);
        doNothing().when(dynamoDbUtil).saveUser(any(User.class));

        String result = userService.register(user);
        assertEquals("User registered successfully.", result);
        assertNotNull(user.getPassword()); // Password should be hashed
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }

    @Test
    void testLoginUserNotFound() {
        String username = "nonExistentUser";
        String password = "password123";

        when(dynamoDbUtil.getUserByUsername(username)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(username, password);
        });

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    void testLoginWithIncorrectPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("correctPassword"));

        when(dynamoDbUtil.getUserByUsername("testuser")).thenReturn(user);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "wrongPassword");
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testSuccessfulLogin() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("correctPassword"));

        when(dynamoDbUtil.getUserByUsername("testuser")).thenReturn(user);
        when(jwtUtil.generateToken("testuser")).thenReturn("dummyToken");

        String token = userService.login("testuser", "correctPassword");

        assertEquals("dummyToken", token);
        verify(jwtUtil, times(1)).generateToken("testuser");
    }
}
