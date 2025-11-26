package com.motherskitchen.backend.DTO.Inventory;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private UUID id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String image;
    private Boolean isActive;
}
