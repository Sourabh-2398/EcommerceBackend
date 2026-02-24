package com.example.ecommercebackend.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for inventory batch information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory batch details")
public class InventoryBatchDTO {

    @Schema(description = "Batch ID", example = "1")
    private Long batchId;

    @Schema(description = "Quantity available in this batch", example = "50")
    private Integer quantity;

    @Schema(description = "Expiry date of the batch", example = "2025-12-31")
    private LocalDate expiryDate;
}

