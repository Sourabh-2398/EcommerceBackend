package com.example.ecommercebackend;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot application for E-commerce Microservices.
 * Implements Order Service and Inventory Service for managing product orders and inventory.
 * Services:
 * - Inventory Service: Manages product inventory with batch tracking and expiry dates
 * - Order Service: Processes customer orders with real-time inventory checks
 * Features:
 * - Factory Pattern for extensible inventory strategies
 * - RESTful APIs for inter-service communication
 * - H2 in-memory database with Liquibase migrations
 * - Comprehensive unit and integration testing
 * - Swagger/OpenAPI documentation
 */
@SpringBootApplication
public class EcoomerceBackend {

    public static void main(String[] args) {
        SpringApplication.run(EcoomerceBackend.class, args);
    }

    /**
     * Configure OpenAPI/Swagger documentation.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce Microservices API")
                        .version("1.0.0")
                        .description("REST APIs for Order and Inventory Services\n\n" +
                                "Microservices:\n" +
                                "- Inventory Service: /inventory\n" +
                                "- Order Service: /order\n\n" +
                                "Database: H2 (http://localhost:8080/h2-console)\n" +
                                "Swagger UI: http://localhost:8080/swagger-ui.html"));
    }
}

