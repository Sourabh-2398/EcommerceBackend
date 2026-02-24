package com.example.ecommercebackend.order.controller;

import com.example.ecommercebackend.order.dto.OrderRequestDTO;
import com.example.ecommercebackend.order.dto.OrderResponseDTO;
import com.example.ecommercebackend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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
 * REST controller for Order Service.
 * Provides endpoints for order placement and management.
 */
@RestController
@RequestMapping("/order")
@Tag(name = "Order Service", description = "APIs for managing orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Place a new order for a product.
     * Checks inventory availability and reserves stock.
     *
     * @param orderRequest the order request
     * @return order response with order details
     */
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Places a new order and reserves inventory from available batches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient inventory"),
            @ApiResponse(responseCode = "500", description = "Failed to place order")
    })
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestBody OrderRequestDTO orderRequest) {
        log.info("POST request to place order for product ID: {} with quantity: {}",
                orderRequest.getProductId(), orderRequest.getQuantity());

        try {
            OrderResponseDTO response = orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid order request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to place order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

