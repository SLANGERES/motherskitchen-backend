package com.motherskitchen.backend.Configuration;

import com.motherskitchen.backend.Security.JwtAuthenticationFilter;
import com.motherskitchen.backend.Security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    // ----------------------------------------------------------------------
    // SECURITY FILTER CHAIN
    // ----------------------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Allow OPTIONS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public routes
                        .requestMatchers(
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/party-order",
                                "/api/v1/health",
                                "/api/v1/inventory/active",
                                "/api/v1/inventory/top-product"
                        ).permitAll()

                        // Admin-only routes
                        .requestMatchers(
                                "/api/v1/inventory/add",
                                "/api/v1/inventory/delete/**",
                                "/api/v1/inventory/deactivate/**",
                                "/api/v1/inventory/activate/**",
                                "/api/v1/orders/all-orders",
                                "/api/v1/orders/status/**",
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")

                        // Everything else requires login
                        .anyRequest().authenticated()
                )

                // No session — using JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Register JWT filter before username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ----------------------------------------------------------------------
    // CORS CONFIGURATION (FULLY FIXED)
    // ----------------------------------------------------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // MUST be first
        configuration.setAllowCredentials(true);

        // Allow your domain, subdomain & locals
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://motherskitchen.se",
                "https://www.motherskitchen.se",
                "https://api.motherskitchen.se"
        ));

        // Which headers are allowed
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Expose headers to JS
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
