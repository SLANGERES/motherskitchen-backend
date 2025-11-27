package com.motherskitchen.backend.Security;

import com.motherskitchen.backend.Security.Jwt.AuthUtil;
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

    // PUBLIC APIs
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
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // PUBLIC ROUTES SKIPPED
        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = null;

        try {
            Claims claims = authUtil.getClaimsFromToken(token);
            email = claims.getSubject();
        } catch (Exception e) {
            System.out.println("Invalid Token: " + e.getMessage());
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (authUtil.validateToken(token)) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublic(String path) {

        if (PUBLIC_PATHS.contains(path)) return true;

        // Entire inventory public except admin APIs
        if (path.startsWith("/api/v1/inventory")) return true;

        return false;
    }

    private String extractToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer "))
            return header.substring(7);

        // Check cookies
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}
