package com.example.ecommercebackend.order.service;

import com.example.ecommercebackend.inventory.dto.InventoryResponseDTO;
import com.example.ecommercebackend.inventory.dto.InventoryBatchDTO;
import com.example.ecommercebackend.order.dto.OrderRequestDTO;
import com.example.ecommercebackend.order.dto.OrderResponseDTO;
import com.example.ecommercebackend.order.entity.Order;
import com.example.ecommercebackend.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDTO orderRequest;
    private InventoryResponseDTO inventoryResponse;

    @BeforeEach
    void setUp() {
        orderRequest = OrderRequestDTO.builder()
                .productId(1001L)
                .quantity(10)
                .build();

        inventoryResponse = InventoryResponseDTO.builder()
                .productId(1001L)
                .productName("Laptop")
                .batches(Arrays.asList(
                        InventoryBatchDTO.builder()
                                .batchId(1L)
                                .quantity(50)
                                .expiryDate(LocalDate.of(2026, 6, 25))
                                .build(),
                        InventoryBatchDTO.builder()
                                .batchId(2L)
                                .quantity(30)
                                .expiryDate(LocalDate.of(2026, 3, 15))
                                .build()
                ))
                .totalQuantity(80)
                .build();
    }

    @Test
    void testPlaceOrder_Success() {
        when(restTemplate.getForObject(anyString(), eq(InventoryResponseDTO.class)))
                .thenReturn(inventoryResponse);

        Order savedOrder = Order.builder()
                .orderId(1L)
                .productId(1001L)
                .productName("Laptop")
                .quantity(10)
                .status("PLACED")
                .orderDate(LocalDate.now())
                .reservedBatchIds("1,2")
                .build();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(restTemplate.postForObject(anyString(), any(), eq(Void.class)))
                .thenReturn(null);

        OrderResponseDTO response = orderService.placeOrder(orderRequest);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals("Laptop", response.getProductName());
        assertEquals(10, response.getQuantity());
        assertEquals("PLACED", response.getStatus());
        assertEquals("Order placed. Inventory reserved.", response.getMessage());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(restTemplate, atLeastOnce()).postForObject(anyString(), any(), eq(Void.class));
    }

    @Test
    void testPlaceOrder_InsufficientInventory() {
        inventoryResponse.setTotalQuantity(5); // Less than requested

        when(restTemplate.getForObject(anyString(), eq(InventoryResponseDTO.class)))
                .thenReturn(inventoryResponse);

        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(orderRequest));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_InventoryServiceUnavailable() {
        when(restTemplate.getForObject(anyString(), eq(InventoryResponseDTO.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(Exception.class, () -> orderService.placeOrder(orderRequest));

        verify(orderRepository, never()).save(any(Order.class));
    }
}

