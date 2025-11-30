package com.motherskitchen.backend.DTO.Inventory;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private double price;

    @NotBlank
    private String category;


    private String imageURL;
    private String imageKey;

}
