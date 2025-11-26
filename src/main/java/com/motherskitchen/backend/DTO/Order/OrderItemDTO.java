package com.motherskitchen.backend.DTO.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class OrderItemDTO {

    @NotBlank
    private UUID itemId;
    @NotBlank

    private String name;
    private Integer quantity;
    private Double price;
}
