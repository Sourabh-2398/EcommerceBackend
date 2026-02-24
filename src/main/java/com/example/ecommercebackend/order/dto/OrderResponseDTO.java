package com.example.ecommercebackend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for order response after successful order placement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order response after successful placement")
public class OrderResponseDTO {

    @Schema(description = "Order ID", example = "5012")
    private Long orderId;

    @Schema(description = "Product ID", example = "1002")
    private Long productId;

    @Schema(description = "Product name", example = "Smartphone")
    private String productName;

    @Schema(description = "Quantity ordered", example = "3")
    private Integer quantity;

    @Schema(description = "Order status", example = "PLACED")
    private String status;

    @Schema(description = "List of batch IDs from which inventory was reserved")
    private List<Long> reservedFromBatchIds;

    @Schema(description = "Response message", example = "Order placed. Inventory reserved.")
    private String message;
}

