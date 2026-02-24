package com.example.ecommercebackend.inventory.controller;

import com.example.ecommercebackend.inventory.dto.InventoryResponseDTO;
import com.example.ecommercebackend.inventory.dto.InventoryUpdateDTO;
import com.example.ecommercebackend.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Inventory Service.
 * Provides endpoints for inventory queries and updates.
 */
@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory Service", description = "APIs for managing inventory and batches")
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Get inventory for a specific product with batches sorted by expiry date.
     *
     * @param productId the product ID
     * @return inventory response with batches sorted by expiry date
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID",
            description = "Returns inventory batches for a product sorted by expiry date (earliest first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<InventoryResponseDTO> getInventoryByProduct(
            @Parameter(description = "Product ID", example = "1001")
            @PathVariable Long productId) {
        log.info("GET request for inventory of product ID: {}", productId);
        InventoryResponseDTO response = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update inventory after an order is placed.
     * Reduces quantity from specified batches.
     *
     * @param updateDTO the inventory update request
     * @return response status
     */
    @PostMapping("/update")
    @Operation(summary = "Update inventory",
            description = "Updates inventory by reducing quantity from specified batches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Failed to update inventory")
    })
    public ResponseEntity<String> updateInventory(
            @RequestBody InventoryUpdateDTO updateDTO) {
        log.info("POST request to update inventory for product ID: {}", updateDTO.getProductId());

        boolean success = inventoryService.updateInventory(updateDTO);

        if (success) {
            return ResponseEntity.ok("Inventory updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update inventory - insufficient quantity in specified batches");
        }
    }

    /**
     * Check if sufficient inventory is available.
     * This is an internal endpoint used by Order Service.
     *
     * @param productId the product ID
     * @param quantity the required quantity
     */
    @GetMapping("/check/{productId}/{quantity}")
    @Operation(summary = "Check inventory availability",
            description = "Checks if sufficient inventory is available for a product")
    @ApiResponse(responseCode = "200", description = "Inventory check completed")
    public ResponseEntity<Boolean> checkAvailability(
            @Parameter(description = "Product ID")
            @PathVariable Long productId,
            @Parameter(description = "Required quantity")
            @PathVariable Integer quantity) {
        log.info("Checking availability for product ID: {} with quantity: {}", productId, quantity);
        boolean available = inventoryService.isSufficientInventory(productId, quantity);
        return ResponseEntity.ok(available);
    }
}

