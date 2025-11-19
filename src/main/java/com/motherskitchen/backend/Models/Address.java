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
@Embeddable
public class Address {

    private String streetAddress;

    private String city;

    private String postalcode;

}
