package com.motherskitchen.backend.DTO.User;

import com.motherskitchen.backend.DTO.Order.OrdersCreateDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String email;


    @NotBlank
    private String password;

    @NotBlank
    private String phone;

}
