// JwtAuthenticationFilter.java
package com.motherskitchen.backend.Security;

import com.motherskitchen.backend.Security.Jwt.AuthUtil;
import com.motherskitchen.backend.Security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(AuthUtil authUtil, UserDetailsServiceImpl userDetailsService) {
        this.authUtil = authUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;

        // 1. Check for JWT in Authorization header first
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("JWT found in Authorization header");
        } else {
            // 2. If no Authorization header, check cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        System.out.println("JWT found in cookie: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
                        break;
                    }
                }
            }
        }

        // If no JWT found in either location, continue without authentication
        if (jwt == null) {
            System.out.println("No JWT token found in request to: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extract username (subject) from JWT
            Claims claims = authUtil.getClaimsFromToken(jwt);
            userEmail = claims.getSubject();
            System.out.println("Extracted email from JWT: " + userEmail);
        } catch (Exception e) {
            // Log the exception (expired, signature invalid, etc.)
            System.err.println("JWT processing error: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Validate Token and Authentication Status
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                System.out.println("Loaded user details for: " + userEmail);

                // Assuming you added a validateToken() method to AuthUtil
                if (authUtil.validateToken(jwt)) {
                    // 4. Create and set Authentication object
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Set the Authentication in the Security Context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication successful for: " + userEmail);
                } else {
                    System.err.println("Token validation failed");
                }
            } catch (Exception e) {
                System.err.println("Error loading user details: " + e.getMessage());
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}