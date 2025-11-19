package com.motherskitchen.backend.DTO.Order;


import com.motherskitchen.backend.Models.Address;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrdersDTO {

    private UUID id;
    private UUID customerId;
    private Double totalAmount;
    private String status;

    private List<OrderItemDTO> items;
    private Address address;
}
