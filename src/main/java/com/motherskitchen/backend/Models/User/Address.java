package com.motherskitchen.backend.Models.User;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String streetAddress;

    private String city;

    private String postalcode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
