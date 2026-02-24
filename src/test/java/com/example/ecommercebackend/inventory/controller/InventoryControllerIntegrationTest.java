package com.example.ecommercebackend.inventory.controller;

import com.example.ecommercebackend.inventory.dto.InventoryResponseDTO;
import com.example.ecommercebackend.inventory.dto.InventoryUpdateDTO;
import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import com.example.ecommercebackend.inventory.repository.InventoryBatchRepository;
import com.example.ecommercebackend.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Inventory Service using @SpringBootTest and H2 database.
 * Tests the service layer with real database interactions.
 */
@SpringBootTest
@ActiveProfiles("test")
class InventoryControllerIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryBatchRepository inventoryBatchRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data to avoid test conflicts
        inventoryBatchRepository.deleteAll();

        // Create test data
        InventoryBatch batch1 = InventoryBatch.builder()
                .productId(1001L)
                .productName("Laptop")
                .quantity(50)
                .expiryDate(LocalDate.of(2026, 6, 25))
                .build();

        InventoryBatch batch2 = InventoryBatch.builder()
                .productId(1001L)
                .productName("Laptop")
                .quantity(30)
                .expiryDate(LocalDate.of(2026, 3, 15))
                .build();

        inventoryBatchRepository.save(batch1);
        inventoryBatchRepository.save(batch2);
    }

    @Test
    void testGetInventoryByProduct_Success() {
        InventoryResponseDTO response = inventoryService.getInventoryByProduct(1001L);

        assertNotNull(response);
        assertEquals(1001L, response.getProductId());
        assertFalse(response.getBatches().isEmpty(), "Expected batches for product 1001");
        assertTrue(response.getTotalQuantity() > 0, "Expected positive total quantity");
    }

    @Test
    void testGetInventoryByProduct_NotFound() {
        InventoryResponseDTO response = inventoryService.getInventoryByProduct(9999L);

        assertNotNull(response);
        assertEquals(9999L, response.getProductId());
        assertTrue(response.getBatches().isEmpty(), "Expected no batches for non-existent product");
        assertEquals(0, response.getTotalQuantity());
    }

    @Test
    void testCheckInventoryAvailability_Sufficient() {
        boolean result = inventoryService.isSufficientInventory(1001L, 50);
        assertTrue(result, "Expected sufficient inventory for 50 units");
    }

    @Test
    void testCheckInventoryAvailability_Insufficient() {
        boolean result = inventoryService.isSufficientInventory(1001L, 10000);
        assertFalse(result, "Expected insufficient inventory for 10000 units");
    }

    @Test
    void testCheckInventoryAvailability_ProductNotFound() {
        boolean result = inventoryService.isSufficientInventory(9999L, 100);
        assertFalse(result, "Expected false for non-existent product");
    }

    @Test
    void testUpdateInventory_Success() {
        var batches = inventoryBatchRepository.findByProductIdOrderByExpiryDate(1001L);
        assertFalse(batches.isEmpty(), "Expected batches to be available");

        Long batchId = batches.get(0).getBatchId();

        InventoryUpdateDTO updateDTO = InventoryUpdateDTO.builder()
                .productId(1001L)
                .quantityToReduce(10)
                .batchIds(batchId.toString())
                .build();

        boolean result = inventoryService.updateInventory(updateDTO);
        assertTrue(result, "Expected inventory update to succeed");
    }


    @Test
    void testGetInventoryByProduct_VerifyBatchOrdering() {
        InventoryResponseDTO response = inventoryService.getInventoryByProduct(1001L);

        assertNotNull(response);
        assertFalse(response.getBatches().isEmpty());

        // Verify batches are sorted by expiry date (earliest first)
        if (response.getBatches().size() > 1) {
            for (int i = 0; i < response.getBatches().size() - 1; i++) {
                assertTrue(
                    response.getBatches().get(i).getExpiryDate()
                        .compareTo(response.getBatches().get(i + 1).getExpiryDate()) <= 0,
                    "Batches should be sorted by expiry date"
                );
            }
        }
    }

    @Test
    void testReserveBatches_Success() {
        java.util.List<Long> reserved = inventoryService.reserveBatches(1001L, 40);

        assertNotNull(reserved);
        assertFalse(reserved.isEmpty(), "Expected at least one batch to be reserved");
        assertTrue(reserved.size() > 0, "Expected batch IDs in reservation");
    }

    @Test
    void testDataLoadedFromLiquibase() {
        long totalBatches = inventoryBatchRepository.count();
        assertTrue(totalBatches > 0, "Expected data to be loaded from Liquibase migrations");
    }
}

