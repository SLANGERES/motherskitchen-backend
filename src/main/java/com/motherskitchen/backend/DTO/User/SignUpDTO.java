package com.motherskitchen.backend.DTO.User;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {

    private String name;
    private String email;
    private String password;
    private String phoneNo;
    private List<AddressesDTO> addresses;

}
