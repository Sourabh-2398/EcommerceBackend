package com.example.ecommercebackend.inventory.service;

import com.example.ecommercebackend.inventory.dto.InventoryBatchDTO;
import com.example.ecommercebackend.inventory.dto.InventoryResponseDTO;
import com.example.ecommercebackend.inventory.dto.InventoryUpdateDTO;
import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import com.example.ecommercebackend.inventory.factory.InventoryStrategy;
import com.example.ecommercebackend.inventory.factory.InventoryStrategyFactory;
import com.example.ecommercebackend.inventory.repository.InventoryBatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing inventory operations.
 * Handles inventory queries, updates, and batch reservations.
 */
@Service
@Slf4j
public class InventoryService {

    private final InventoryBatchRepository inventoryBatchRepository;
    private final InventoryStrategyFactory strategyFactory;

    @Autowired
    public InventoryService(InventoryBatchRepository inventoryBatchRepository,
                            InventoryStrategyFactory strategyFactory) {
        this.inventoryBatchRepository = inventoryBatchRepository;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Get inventory for a specific product sorted by expiry date.
     *
     * @param productId the product ID
     * @return inventory response with batches sorted by expiry date
     */
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryByProduct(Long productId) {
        log.info("Fetching inventory for product ID: {}", productId);

        List<InventoryBatch> batches = inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId);

        if (batches.isEmpty()) {
            log.warn("No inventory found for product ID: {}", productId);
            return InventoryResponseDTO.builder()
                    .productId(productId)
                    .productName("Unknown")
                    .batches(List.of())
                    .totalQuantity(0)
                    .build();
        }

        // Use default strategy to get available inventory
        InventoryStrategy strategy = strategyFactory.getStrategy("DEFAULT");
        List<InventoryBatch> availableBatches = strategy.getAvailableInventory(batches);
        Integer totalQuantity = strategy.calculateTotalQuantity(availableBatches);

        List<InventoryBatchDTO> batchDTOs = availableBatches.stream()
                .map(batch -> InventoryBatchDTO.builder()
                        .batchId(batch.getBatchId())
                        .quantity(batch.getQuantity())
                        .expiryDate(batch.getExpiryDate())
                        .build())
                .collect(Collectors.toList());

        return InventoryResponseDTO.builder()
                .productId(productId)
                .productName(batches.get(0).getProductName())
                .batches(batchDTOs)
                .totalQuantity(totalQuantity)
                .build();
    }

    /**
     * Update inventory after an order is placed.
     * Reduces quantity from specified batches.
     *
     * @param updateDTO the inventory update request
     * @return true if update was successful, false otherwise
     */
    @Transactional
    public boolean updateInventory(InventoryUpdateDTO updateDTO) {
        log.info("Updating inventory for product ID: {} with quantity: {}",
                updateDTO.getProductId(), updateDTO.getQuantityToReduce());

        // Parse batch IDs
        List<Long> batchIds = Arrays.stream(updateDTO.getBatchIds().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();

        int remainingQuantity = updateDTO.getQuantityToReduce();

        // Reduce inventory from batches in order
        for (Long batchId : batchIds) {
            InventoryBatch batch = inventoryBatchRepository.findByBatchId(batchId);
            if (batch == null) {
                log.warn("Batch ID {} not found", batchId);
                continue;
            }

            if (batch.getQuantity() >= remainingQuantity) {
                batch.setQuantity(batch.getQuantity() - remainingQuantity);
                inventoryBatchRepository.save(batch);
                log.info("Reduced quantity {} from batch ID {}", remainingQuantity, batchId);
                remainingQuantity = 0;
                break;
            } else {
                remainingQuantity -= batch.getQuantity();
                batch.setQuantity(0);
                inventoryBatchRepository.save(batch);
                log.info("Exhausted batch ID {}, remaining quantity to reduce: {}", batchId, remainingQuantity);
            }
        }

        if (remainingQuantity > 0) {
            log.warn("Could not reduce all quantity. Remaining: {}", remainingQuantity);
            return false;
        }

        return true;
    }

    /**
     * Check if sufficient inventory is available for a product.
     *
     * @param productId the product ID
     * @param requiredQuantity the required quantity
     * @return true if sufficient inventory is available, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isSufficientInventory(Long productId, Integer requiredQuantity) {
        List<InventoryBatch> batches = inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId);

        InventoryStrategy strategy = strategyFactory.getStrategy("DEFAULT");
        List<InventoryBatch> availableBatches = strategy.getAvailableInventory(batches);
        Integer totalQuantity = strategy.calculateTotalQuantity(availableBatches);

        boolean sufficient = totalQuantity >= requiredQuantity;
        log.info("Checking inventory for product ID: {}. Required: {}, Available: {}, Sufficient: {}",
                productId, requiredQuantity, totalQuantity, sufficient);

        return sufficient;
    }

    /**
     * Reserve batches for an order based on required quantity.
     * Returns the list of batch IDs from which inventory should be reserved.
     *
     * @param productId the product ID
     * @param requiredQuantity the required quantity
     * @return list of batch IDs to reserve from
     */
    @Transactional(readOnly = true)
    public List<Long> reserveBatches(Long productId, Integer requiredQuantity) {
        List<InventoryBatch> batches = inventoryBatchRepository.findByProductIdOrderByExpiryDate(productId);

        List<Long> reservedBatchIds = new java.util.ArrayList<>();
        int remainingQuantity = requiredQuantity;

        for (InventoryBatch batch : batches) {
            if (remainingQuantity <= 0) break;

            if (batch.getQuantity() > 0) {
                reservedBatchIds.add(batch.getBatchId());
                remainingQuantity -= Math.min(batch.getQuantity(), remainingQuantity);
            }
        }

        log.info("Reserved batches {} for product ID: {} with quantity: {}",
                reservedBatchIds, productId, requiredQuantity);

        return reservedBatchIds;
    }
}

