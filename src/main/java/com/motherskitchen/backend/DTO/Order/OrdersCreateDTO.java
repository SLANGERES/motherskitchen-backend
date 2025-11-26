package com.motherskitchen.backend.DTO.Order;


import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class OrdersCreateDTO {

    private String name;
    private String email;
    private String phone;

    private List<OrderItemDTO> items;
    private LocalDate deliveryDate;
    private AddressDTO address;
    private String notes;

}
