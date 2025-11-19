package com.motherskitchen.backend.DTO.Order;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

    private UUID id;

    private String streetAddress;

    private String city;

    private String postalcode;
}
