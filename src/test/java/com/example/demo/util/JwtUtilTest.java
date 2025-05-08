package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.demo.util.JwtUtil;

public class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // JWTs typically start with 'ey'
    }

    @Test
    void testValidateToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token, username));
        assertFalse(jwtUtil.validateToken(token, "wrongUser"));
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertEquals(username, jwtUtil.extractUsername(token));
    }
}
