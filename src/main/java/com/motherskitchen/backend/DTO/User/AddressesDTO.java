package com.motherskitchen.backend.DTO.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressesDTO {

    private String streetAddress;
    private String city;
    private String postalcode;
}
