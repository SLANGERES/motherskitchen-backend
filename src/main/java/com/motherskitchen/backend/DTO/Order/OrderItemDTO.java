package com.motherskitchen.backend.DTO.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class OrderItemDTO {

    private UUID id;
    @NotBlank
    private UUID itemId;
    @NotBlank
    private Integer quantity;
    private Double price;
}
