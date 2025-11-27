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
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // All PUBLIC routes → no JWT checking
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/signup",
            "/api/v1/health",
            "/api/v1/party-order",
            "/api/v1/inventory/active",
            "/api/v1/inventory/top-product"

    );

    public JwtAuthenticationFilter(AuthUtil authUtil, UserDetailsServiceImpl userDetailsService) {
        this.authUtil = authUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1️⃣ Bypass all public routes
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = extractToken(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userEmail = null;

        try {
            Claims claims = authUtil.getClaimsFromToken(jwt);
            userEmail = claims.getSubject();
        } catch (Exception e) {
            System.err.println("JWT parsing failed: " + e.getMessage());
        }

        // 3️⃣ Authenticate if valid
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (authUtil.validateToken(jwt)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception e) {
                System.err.println("Error loading user details: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    // Helper: Skip JWT for these routes
    private boolean isPublicPath(String path) {
        if (PUBLIC_PATHS.contains(path)) return true;
        // Entire inventory public
        if (path.startsWith("/api/v1/inventory")) return true;
        return false;
    }

    // Extract token from header or cookie
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Check cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}
