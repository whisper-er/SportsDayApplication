package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister_Success() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmailId("test@example.com");

        when(userService.register(any(User.class))).thenReturn("User registered successfully.");

        ResponseEntity<Map<String, String>> response = userController.register(user);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody().get("message"));
    }

    @Test
    public void testRegister_Failure() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmailId("invalidEmail");

        when(userService.register(any(User.class))).thenReturn("Invalid email format.");

        ResponseEntity<Map<String, String>> response = userController.register(user);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email format.", response.getBody().get("message"));
    }

    @Test
    public void testLogin_Success() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        when(userService.login("testUser", "password")).thenReturn("fakeJwtToken");

        ResponseEntity<Map<String, String>> response = userController.login(user);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successful login", response.getBody().get("message"));
        assertEquals("fakeJwtToken", response.getBody().get("token"));
    }

    @Test
    public void testLogin_Failure() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("wrongPassword");

        when(userService.login("testUser", "wrongPassword")).thenThrow(new RuntimeException("Invalid username or password"));

        ResponseEntity<Map<String, String>> response = userController.login(user);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("error"));
    }

    @Test
    public void testLogout() {
        ResponseEntity<Map<String, String>> response = userController.logout();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful.", response.getBody().get("message"));
    }
}
