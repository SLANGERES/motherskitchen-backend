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
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

// Consider adding an SLF4J logger here for better error reporting

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserDetailsServiceImpl userDetailsService;

    // It's good practice to ensure the secret key is long enough (e.g., 256 bits or 32 characters)
    @Value("${jwt.master_key}")
    private String jwtSecretKey;

    /**
     * Retrieves the SecretKey from the base64-encoded string.
     * Use StandardCharsets.UTF_8 for consistent byte representation.
     */
    private SecretKey getSecretKey() {
        // JJWT recommends at least 256 bits (32 bytes) for HMAC-SHA
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a new JWT access token for a Donor user.
     * Token expiration is now set to 24 hours.
     */
    public String generateAccessToken(UserDTO user) {
        long now = System.currentTimeMillis();
        long expiration = now + 1000L * 60 * 60 * 24; // 24 hours

        // 🔥 Load actual SPRING SECURITY user (MASTER ADMIN or DB USER)
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(now))
                .expiration(new Date(expiration))

                .claim("uid", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("phone_no", user.getPhoneNo())

                // 🔥 REAL ROLES HERE
                .claim("roles", userDetails.getAuthorities())

                .signWith(getSecretKey())
                .compact();
    }


    /**
     * Extracts Claims (payload) from a signed JWT token.
     * @param token The JWT string.
     * @return The claims payload.
     */
    public Claims getClaimsFromToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }

    /**
     * Validates a JWT token by checking its signature and expiration.
     * @param token The JWT string.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            // Attempt to parse and verify the token
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException e) {
            // Token is invalid (e.g., ExpiredJwtException, SignatureException, MalformedJwtException)
            // It is highly recommended to log the specific exception (e.g., log.warn("JWT validation failed: {}", e.getMessage()))
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
}