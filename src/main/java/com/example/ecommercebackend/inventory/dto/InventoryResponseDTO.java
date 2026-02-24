package com.example.ecommercebackend.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for inventory response containing product details and available batches.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory response with batches sorted by expiry date")
public class InventoryResponseDTO {

    @Schema(description = "Product ID", example = "1001")
    private Long productId;

    @Schema(description = "Product name", example = "Laptop")
    private String productName;

    @Schema(description = "List of batches sorted by expiry date (earliest first)")
    private List<InventoryBatchDTO> batches;

    @Schema(description = "Total available quantity across all batches", example = "100")
    private Integer totalQuantity;
}

