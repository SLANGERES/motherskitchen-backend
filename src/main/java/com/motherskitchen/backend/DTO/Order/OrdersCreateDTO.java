package com.motherskitchen.backend.DTO.Order;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
public class OrdersCreateDTO {

    private UUID customerId;
    private List<OrderItemDTO> items;
    private AddressDTO addressDTO;

}
