package com.example.ecommercebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for REST communication between microservices.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Create a RestTemplate bean for inter-service communication.
     *
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

