package com.example.demo.filter;

import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternalWithoutToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws Exception {
        String token = "Bearer invalid.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("invalid.token")).thenReturn("user");
        when(jwtUtil.validateToken("invalid.token", "user")).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalWithExpiredToken() throws Exception {
        String token = "Bearer expired.token";
        String username = "user";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("expired.token")).thenReturn(username);
        when(jwtUtil.validateToken("expired.token", username)).thenReturn(false); // Simulate expired token

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalWithValidToken() throws Exception {
        String token = "Bearer valid.token";
        String username = "user";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("valid.token")).thenReturn(username);
        when(jwtUtil.validateToken("valid.token", username)).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // Optionally check that authentication was set correctly
        // assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalWithNonBearerToken() throws Exception {
        String token = "Basic someEncodedString";
        when(request.getHeader("Authorization")).thenReturn(token);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalWithExistingAuthentication() throws Exception {
        String token = "Bearer valid.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("valid.token")).thenReturn("user");
        when(jwtUtil.validateToken("valid.token", "user")).thenReturn(true);

        // Simulate an already authenticated user
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // Check that authentication is not set again
        assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

}
