package com.example.ecommercebackend.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating inventory after an order is placed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update inventory after order placement")
public class InventoryUpdateDTO {

    @Schema(description = "Product ID", example = "1001", required = true)
    private Long productId;

    @Schema(description = "Quantity to reduce from inventory", example = "5", required = true)
    private Integer quantityToReduce;

    @Schema(description = "List of batch IDs from which to reduce inventory (comma-separated)",
            example = "1,2", required = true)
    private String batchIds;
}

