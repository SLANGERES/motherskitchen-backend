package com.motherskitchen.backend.DTO.Party;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartyOrderRequest {
    private String name;
    private String email;
    private String phone;
    private String date;
    private String guests;
    private String combo;
    private String vegNonVeg;
    private String message;
}
