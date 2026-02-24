package com.example.ecommercebackend.inventory.repository;

import com.example.ecommercebackend.inventory.entity.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InventoryBatch entity providing database operations.
 */
@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

    /**
     * Find all batches for a given product, sorted by expiry date (earliest first).
     *
     * @param productId the product ID
     * @return list of batches sorted by expiry date
     */
    @Query("SELECT ib FROM InventoryBatch ib WHERE ib.productId = :productId ORDER BY ib.expiryDate ASC")
    List<InventoryBatch> findByProductIdOrderByExpiryDate(@Param("productId") Long productId);

    /**
     * Find a batch by batch ID.
     *
     * @param batchId the batch ID
     * @return the inventory batch if found
     */
    @Query("SELECT ib FROM InventoryBatch ib WHERE ib.batchId = :batchId")
    InventoryBatch findByBatchId(@Param("batchId") Long batchId);
}

