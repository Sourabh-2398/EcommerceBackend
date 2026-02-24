package com.example.ecommercebackend.inventory.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating inventory strategy implementations.
 * Allows runtime selection of different inventory handling strategies.
 */
@Component
public class InventoryStrategyFactory {

    private final DefaultInventoryStrategy defaultStrategy;
    private final ExpiryPriorityInventoryStrategy expiryPriorityStrategy;

    @Autowired
    public InventoryStrategyFactory(DefaultInventoryStrategy defaultStrategy,
                                   ExpiryPriorityInventoryStrategy expiryPriorityStrategy) {
        this.defaultStrategy = defaultStrategy;
        this.expiryPriorityStrategy = expiryPriorityStrategy;
    }

    /**
     * Get inventory strategy based on type.
     *
     * @param strategyType the type of strategy ("DEFAULT" or "EXPIRY_PRIORITY")
     * @return the corresponding inventory strategy
     */
    public InventoryStrategy getStrategy(String strategyType) {
        if(strategyType.equalsIgnoreCase("EXPIRY_PRIORITY")) {
            return expiryPriorityStrategy;
        } else{
            return defaultStrategy;
        }
    }
}

