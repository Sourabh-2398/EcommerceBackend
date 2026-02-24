package com.example.ecommercebackend.order.service;

import com.example.ecommercebackend.inventory.dto.InventoryResponseDTO;
import com.example.ecommercebackend.inventory.dto.InventoryUpdateDTO;
import com.example.ecommercebackend.order.dto.OrderRequestDTO;
import com.example.ecommercebackend.order.dto.OrderResponseDTO;
import com.example.ecommercebackend.order.entity.Order;
import com.example.ecommercebackend.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing order operations.
 * Communicates with Inventory Service to check availability and reserve stock.
 */
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${inventory.service.url:http://localhost:8080}")
    private String inventoryServiceUrl;

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Place a new order for a product.
     * Checks inventory availability and updates stock accordingly.
     *
     * @param orderRequest the order request containing product ID and quantity
     * @return order response with order details
     * @throws IllegalArgumentException if inventory is insufficient
     * @throws RestClientException if inventory service communication fails
     */
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) {
        log.info("Placing order for product ID: {} with quantity: {}",
                orderRequest.getProductId(), orderRequest.getQuantity());

        // Check inventory availability
        InventoryResponseDTO inventory = checkInventoryAvailability(orderRequest.getProductId());

        if (inventory == null || inventory.getTotalQuantity() < orderRequest.getQuantity()) {
            log.error("Insufficient inventory for product ID: {}. Required: {}, Available: {}",
                    orderRequest.getProductId(), orderRequest.getQuantity(),
                    inventory != null ? inventory.getTotalQuantity() : 0);
            throw new IllegalArgumentException("Insufficient inventory for product ID: " +
                    orderRequest.getProductId());
        }

        // Reserve batches from inventory
        List<Long> reservedBatchIds = reserveBatchesFromInventory(orderRequest.getProductId(),
                orderRequest.getQuantity());

        if (reservedBatchIds.isEmpty()) {
            log.error("Failed to reserve batches for product ID: {}", orderRequest.getProductId());
            throw new IllegalArgumentException("Failed to reserve inventory for product ID: " +
                    orderRequest.getProductId());
        }

        // Create order
        Order order = Order.builder()
                .productId(orderRequest.getProductId())
                .productName(inventory.getProductName())
                .quantity(orderRequest.getQuantity())
                .status("PLACED")
                .orderDate(LocalDate.now())
                .reservedBatchIds(reservedBatchIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")))
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with ID: {}", savedOrder.getOrderId());

        // Update inventory
        updateInventory(orderRequest.getProductId(), orderRequest.getQuantity(), reservedBatchIds);

        return OrderResponseDTO.builder()
                .orderId(savedOrder.getOrderId())
                .productId(savedOrder.getProductId())
                .productName(savedOrder.getProductName())
                .quantity(savedOrder.getQuantity())
                .status(savedOrder.getStatus())
                .reservedFromBatchIds(reservedBatchIds)
                .message("Order placed. Inventory reserved.")
                .build();
    }

    /**
     * Check inventory availability by calling Inventory Service.
     *
     * @param productId the product ID
     * @return inventory response from inventory service
     */
    private InventoryResponseDTO checkInventoryAvailability(Long productId) {
        try {
            String url = inventoryServiceUrl + "/inventory/" + productId;
            log.debug("Calling inventory service: {}", url);

            InventoryResponseDTO response = restTemplate.getForObject(url, InventoryResponseDTO.class);
            log.info("Inventory check successful for product ID: {}", productId);
            return response;
        } catch (RestClientException e) {
            log.error("Failed to check inventory for product ID: {}", productId, e);
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }

    /**
     * Reserve batches by calling Inventory Service.
     * This is a simulated method - in production, this would call a reserve endpoint.
     *
     * @param productId the product ID
     * @param requiredQuantity the required quantity
     * @return list of reserved batch IDs
     */
    private List<Long> reserveBatchesFromInventory(Long productId, Integer requiredQuantity) {
        try {
            InventoryResponseDTO inventory = checkInventoryAvailability(productId);

            if (inventory == null || inventory.getBatches().isEmpty()) {
                return List.of();
            }

            // Reserve batches in order of expiry date
            List<Long> reservedIds = new java.util.ArrayList<>();
            int remaining = requiredQuantity;

            for (var batch : inventory.getBatches()) {
                if (remaining <= 0) break;
                reservedIds.add(batch.getBatchId());
                remaining -= Math.min(batch.getQuantity(), remaining);
            }

            return reservedIds;
        } catch (Exception e) {
            log.error("Failed to reserve batches for product ID: {}", productId, e);
            return List.of();
        }
    }

    /**
     * Update inventory by calling Inventory Service.
     *
     * @param productId the product ID
     * @param quantityToReduce the quantity to reduce
     * @param batchIds the list of batch IDs
     */
    private void updateInventory(Long productId, Integer quantityToReduce, List<Long> batchIds) {
        try {
            String url = inventoryServiceUrl + "/inventory/update";
            InventoryUpdateDTO updateDTO = InventoryUpdateDTO.builder()
                    .productId(productId)
                    .quantityToReduce(quantityToReduce)
                    .batchIds(batchIds.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")))
                    .build();

            log.debug("Updating inventory at: {}", url);
            restTemplate.postForObject(url, updateDTO, Void.class);
            log.info("Inventory updated successfully for product ID: {}", productId);
        } catch (RestClientException e) {
            log.error("Failed to update inventory for product ID: {}", productId, e);
            // In production, consider rollback or compensation logic
            throw new RuntimeException("Failed to update inventory", e);
        }
    }

    /**
     * Get all orders.
     *
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Get order by ID.
     *
     * @param orderId the order ID
     * @return the order if found
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    /**
     * Get orders by product ID.
     *
     * @param productId the product ID
     * @return list of orders for the product
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByProductId(Long productId) {
        return orderRepository.findByProductId(productId);
    }
}

