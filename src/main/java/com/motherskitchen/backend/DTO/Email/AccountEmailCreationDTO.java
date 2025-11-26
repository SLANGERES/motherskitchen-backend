package com.motherskitchen.backend.DTO.Email;

import lombok.*;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEmailCreationDTO {
    private String to;
    private String name;
    private String email;
    private String uid;
}
