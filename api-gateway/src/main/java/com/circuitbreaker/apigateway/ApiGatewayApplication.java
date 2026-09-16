package com.circuitbreaker.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * "CircuitBreaker" API Gateway - the single entry point for all frontend traffic.
 * Week 1: routes to Product / Inventory / Recommendation services via Eureka.
 * Week 2: wraps the Recommendation route in a Resilience4j Circuit Breaker so a
 * slow/overwhelmed Recommendation Engine degrades gracefully instead of taking
 * the whole Checkout/Search flow down with it.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
