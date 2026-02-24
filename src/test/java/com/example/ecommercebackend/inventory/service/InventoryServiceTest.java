package com.example.ecommercebackend.inventory.service;

import com.example.ecommercebackend.inventory.dto.InventoryResponseDTO;
import com.example.ecommercebackend.inventory.dto.InventoryUpdateDTO;
import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import com.example.ecommercebackend.inventory.factory.DefaultInventoryStrategy;
import com.example.ecommercebackend.inventory.factory.InventoryStrategyFactory;
import com.example.ecommercebackend.inventory.repository.InventoryBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryService using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryBatchRepository inventoryBatchRepository;

    @Mock
    private InventoryStrategyFactory strategyFactory;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryBatch batch1;
    private InventoryBatch batch2;
    private DefaultInventoryStrategy defaultStrategy;

    @BeforeEach
    void setUp() {
        batch1 = InventoryBatch.builder()
                .batchId(1L)
                .productId(1001L)
                .productName("Laptop")
                .quantity(50)
                .expiryDate(LocalDate.of(2026, 6, 25))
                .build();

        batch2 = InventoryBatch.builder()
                .batchId(2L)
                .productId(1001L)
                .productName("Laptop")
                .quantity(30)
                .expiryDate(LocalDate.of(2026, 3, 15))
                .build();

        // Create a real instance of DefaultInventoryStrategy for testing
        defaultStrategy = new DefaultInventoryStrategy();
    }

    @Test
    void testGetInventoryByProduct_Success() {
        Long productId = 1001L;
        List<InventoryBatch> batches = Arrays.asList(batch2, batch1); // batch2 has earlier expiry

        when(inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId))
                .thenReturn(batches);
        when(strategyFactory.getStrategy(anyString()))
                .thenReturn(defaultStrategy);

        InventoryResponseDTO response = inventoryService.getInventoryByProduct(productId);

        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals("Laptop", response.getProductName());
        assertFalse(response.getBatches().isEmpty());
        assertEquals(2, response.getBatches().size());

        verify(inventoryBatchRepository, times(1)).findByProductIdOrderByExpiryDate(productId);
    }

    @Test
    void testGetInventoryByProduct_NotFound() {
        Long productId = 9999L;

        when(inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId))
                .thenReturn(List.of());

        InventoryResponseDTO response = inventoryService.getInventoryByProduct(productId);

        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertTrue(response.getBatches().isEmpty());
        assertEquals(0, response.getTotalQuantity());
    }

    @Test
    void testUpdateInventory_Success() {
        InventoryUpdateDTO updateDTO = InventoryUpdateDTO.builder()
                .productId(1001L)
                .quantityToReduce(25)
                .batchIds("1")
                .build();

        InventoryBatch batch = batch1;
        when(inventoryBatchRepository.findByBatchId(1L)).thenReturn(batch);
        when(inventoryBatchRepository.save(any())).thenReturn(batch);

        boolean result = inventoryService.updateInventory(updateDTO);

        assertTrue(result);
        verify(inventoryBatchRepository, times(1)).findByBatchId(1L);
        verify(inventoryBatchRepository, times(1)).save(any());
    }

    @Test
    void testIsSufficientInventory_True() {
        Long productId = 1001L;
        List<InventoryBatch> batches = Arrays.asList(batch1, batch2);

        when(inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId))
                .thenReturn(batches);
        when(strategyFactory.getStrategy(anyString()))
                .thenReturn(defaultStrategy);

        boolean result = inventoryService.isSufficientInventory(productId, 50);

        assertTrue(result);
        verify(inventoryBatchRepository, times(1)).findByProductIdOrderByExpiryDate(productId);
    }

    @Test
    void testIsSufficientInventory_False() {
        Long productId = 1001L;
        List<InventoryBatch> batches = Arrays.asList(batch1, batch2);

        when(inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId))
                .thenReturn(batches);
        when(strategyFactory.getStrategy(anyString()))
                .thenReturn(defaultStrategy);

        boolean result = inventoryService.isSufficientInventory(productId, 100);

        assertFalse(result);
    }

    @Test
    void testReserveBatches_Success() {
        Long productId = 1001L;
        List<InventoryBatch> batches = Arrays.asList(batch2, batch1);

        when(inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId))
                .thenReturn(batches);

        List<Long> reserved = inventoryService.reserveBatches(productId, 40);

        assertNotNull(reserved);
        assertFalse(reserved.isEmpty());
        assertEquals(2, reserved.size());
    }
}

