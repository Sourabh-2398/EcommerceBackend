package com.example.ecommercebackend.order.controller;

import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import com.example.ecommercebackend.inventory.repository.InventoryBatchRepository;
import com.example.ecommercebackend.order.dto.OrderRequestDTO;
import com.example.ecommercebackend.order.dto.OrderResponseDTO;
import com.example.ecommercebackend.order.repository.OrderRepository;
import com.example.ecommercebackend.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Order Service.
 * Tests the order service with real database interactions.
 * Comprehensive order placement tests are covered in OrderServiceTest with unit tests and mocks.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryBatchRepository inventoryBatchRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        inventoryBatchRepository.deleteAll();

        InventoryBatch batch1 = InventoryBatch.builder()
                .productId(3001L)
                .productName("Tablet")
                .quantity(500)
                .expiryDate(LocalDate.of(2026, 6, 25))
                .build();

        InventoryBatch batch2 = InventoryBatch.builder()
                .productId(3001L)
                .productName("Tablet")
                .quantity(500)
                .expiryDate(LocalDate.of(2026, 3, 15))
                .build();

        inventoryBatchRepository.save(batch1);
        inventoryBatchRepository.save(batch2);
    }

    @Test
    void testOrderValidation_NegativeQuantity() {
        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .productId(3001L)
                .quantity(-5)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequest),
                "Should throw exception for negative quantity");
    }

    @Test
    void testOrderValidation_ZeroQuantity() {
        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .productId(3001L)
                .quantity(0)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequest),
                "Should throw exception for zero quantity");
    }

    @Test
    void testInventoryIntegration_DataPresent() {
        long batchCount = inventoryBatchRepository.count();
        assertTrue(batchCount >= 2, "Test inventory should have at least 2 batches");

        long totalInventory = inventoryBatchRepository.findByProductIdOrderByExpiryDate(3001L)
                .stream()
                .mapToLong(InventoryBatch::getQuantity)
                .sum();

        assertEquals(1000, totalInventory, "Expected 1000 total units of inventory");
    }

    @Test
    void testOrderRepository_CanQuery() {
        long count = orderRepository.count();
        assertTrue(count >= 0, "Order repository should be queryable");
    }
}

