package com.motherskitchen.backend.Security.Jwt;

import com.motherskitchen.backend.DTO.User.UserDTO;
import com.motherskitchen.backend.Security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.master_key}")
    private String jwtSecretKey;

    /**
     * Decode the Base64 secret into a secure HS384 key.
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);  // ✔ Correct for HS384 / HS256
    }

    /**
     * Generate JWT (24h expiration)
     */
    public String generateAccessToken(UserDTO user) {

        long now = System.currentTimeMillis();
        long expiration = now + 1000L * 60 * 60 * 24; // 24 hours

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(now))
                .expiration(new Date(expiration))

                .claim("uid", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("phone_no", user.getPhoneNo())
                .claim("roles", userDetails.getAuthorities())

                .signWith(getSecretKey(), Jwts.SIG.HS384)   // ✔ Explicit signing algorithm
                .compact();
    }

    /**
     * Extract claims from token
     */
    public Claims getClaimsFromToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }

    /**
     * Validate the token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
}
