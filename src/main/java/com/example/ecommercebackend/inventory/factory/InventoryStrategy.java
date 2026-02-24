package com.example.ecommercebackend.inventory.factory;

import com.example.ecommercebackend.inventory.entity.InventoryBatch;

import java.util.List;

/**
 * Strategy interface for inventory handling logic.
 * Allows for extensible implementations of inventory management strategies.
 */
public interface InventoryStrategy {

    /**
     * Get available inventory for a product.
     *
     * @param batches the list of inventory batches
     * @return filtered and processed batches
     */
    List<InventoryBatch> getAvailableInventory(List<InventoryBatch> batches);

    /**
     * Calculate total available quantity.
     *
     * @param batches the list of inventory batches
     * @return total quantity
     */
    Integer calculateTotalQuantity(List<InventoryBatch> batches);
}

