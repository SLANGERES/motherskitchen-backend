package com.motherskitchen.backend.Models;

import com.motherskitchen.backend.Models.User.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String streetAddress;

    private String city;

    private String postalcode;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders order;
}
