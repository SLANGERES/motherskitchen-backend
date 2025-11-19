package com.motherskitchen.backend.DTO.User;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private UUID id;
    private String name;
    private String email;
    private String phoneNo;

    private List<AddressesDTO> addresses;
}
