package com.motherskitchen.backend.DTO.Order;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class OrdersDTO {

    private UUID id;
    private UUID customerId;
    private Double totalAmount;
    private String status;

    private List<OrderItemDTO> items;
}
