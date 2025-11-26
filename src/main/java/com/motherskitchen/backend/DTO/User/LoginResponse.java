package com.motherskitchen.backend.DTO.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String role;
}
