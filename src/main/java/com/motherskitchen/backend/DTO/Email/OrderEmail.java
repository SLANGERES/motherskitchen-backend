package com.motherskitchen.backend.DTO.Email;

import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderEmail {
    private String name;
    private OrdersDTO order;
    private String email;
}
