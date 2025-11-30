package com.motherskitchen.backend.DTO.Order;


import com.motherskitchen.backend.Models.Address;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrdersDTO {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Double totalAmount;
    private String status;
    private String deliveryDate;
    private String Notes;
    private String payment ;
    private List<OrderItemDTO> items;
    private Address address;
}
