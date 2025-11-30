package com.motherskitchen.backend.DTO.Order;

import lombok.Data;
import java.util.List;

@Data
public class OrdersCreateDTO {

    private String name;
    private String email;
    private String phone;
    private String payment;

    private List<OrderItemDTO> items;

    // 🔥 UPDATED FIELDS
    private String day;            // Friday / Saturday / Sunday
    private String orderType;      // delivery / pickup
    private int deliveryCharge;    // 30 or 0

    private AddressDTO address;
    private String notes;
}
