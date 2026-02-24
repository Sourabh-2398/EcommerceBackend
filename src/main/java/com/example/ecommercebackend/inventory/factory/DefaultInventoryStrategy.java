package com.example.ecommercebackend.inventory.factory;

import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Default inventory strategy implementation.
 * Returns all batches that haven't expired, sorted by expiry date.
 */
@Component
public class DefaultInventoryStrategy implements InventoryStrategy {

    @Override
    public List<InventoryBatch> getAvailableInventory(List<InventoryBatch> batches) {
        return batches.stream()
                .filter(batch -> batch.getExpiryDate().isAfter(LocalDate.now()) ||
                               batch.getExpiryDate().isEqual(LocalDate.now()))
                .toList();
    }

    @Override
    public Integer calculateTotalQuantity(List<InventoryBatch> batches) {
        return batches.stream()
                .mapToInt(InventoryBatch::getQuantity)
                .sum();
    }
}

