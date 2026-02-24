package com.example.ecommercebackend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order placement request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order placement request")
public class OrderRequestDTO {

    @Schema(description = "Product ID", example = "1002", required = true)
    private Long productId;

    @Schema(description = "Quantity to order", example = "3", required = true)
    private Integer quantity;
}

