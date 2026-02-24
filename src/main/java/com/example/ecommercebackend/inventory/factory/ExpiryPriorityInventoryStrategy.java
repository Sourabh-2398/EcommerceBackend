package com.example.ecommercebackend.inventory.factory;

import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Inventory strategy that prioritizes batches expiring soon (within 30 days).
 * Useful for implementing first-expiry-first-out (FEFO) strategies.
 */
@Component
public class ExpiryPriorityInventoryStrategy implements InventoryStrategy {

    private static final long EXPIRY_THRESHOLD_DAYS = 30;

    @Override
    public List<InventoryBatch> getAvailableInventory(List<InventoryBatch> batches) {
        return batches.stream()
                .filter(batch -> batch.getExpiryDate().isAfter(LocalDate.now()))
                .sorted((b1, b2) -> {
                    // Prioritize batches expiring soon
                    long daysToExpiry1 = ChronoUnit.DAYS.between(LocalDate.now(), b1.getExpiryDate());
                    long daysToExpiry2 = ChronoUnit.DAYS.between(LocalDate.now(), b2.getExpiryDate());

                    boolean exp1Soon = daysToExpiry1 <= EXPIRY_THRESHOLD_DAYS;
                    boolean exp2Soon = daysToExpiry2 <= EXPIRY_THRESHOLD_DAYS;

                    if (exp1Soon && !exp2Soon) return -1;
                    if (!exp1Soon && exp2Soon) return 1;

                    return Long.compare(daysToExpiry1, daysToExpiry2);
                })
                .toList();
    }

    @Override
    public Integer calculateTotalQuantity(List<InventoryBatch> batches) {
        return batches.stream()
                .mapToInt(InventoryBatch::getQuantity)
                .sum();
    }
}

