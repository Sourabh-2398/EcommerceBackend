package com.example.ecommercebackend.order.repository;

import com.example.ecommercebackend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Order entity providing database operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders for a given product ID.
     *
     * @param productId the product ID
     * @return list of orders for the product
     */
    List<Order> findByProductId(Long productId);

    /**
     * Find all orders with a specific status.
     *
     * @param status the order status
     * @return list of orders with the given status
     */
    List<Order> findByStatus(String status);
}

