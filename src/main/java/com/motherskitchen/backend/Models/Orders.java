package com.motherskitchen.backend.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private String phone;

    private Double total;

    private String status;

    private String deliveryDay;     // Friday / Saturday / Sunday

    private String notes;

    private String orderType;       // delivery / pickup

    private int deliveryCharge;     // 30 or 0

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    private String payment;

    @Embedded
    private Address address;
}
