package com.motherskitchen.backend.DTO.Order;

import lombok.Data;
import java.util.UUID;

@Data
public class OrderItemCreateDTO {

    private UUID itemId;
    private Integer quantity;
}
