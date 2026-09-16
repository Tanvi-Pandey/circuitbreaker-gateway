package com.circuitbreaker.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Discovery registry for the CircuitBreaker platform.
 * Every microservice (Product, Inventory, Recommendation) and the API Gateway
 * registers itself here on startup and sends heartbeats. The Gateway looks
 * services up by name (e.g. "PRODUCT-SERVICE") instead of hardcoded host:port,
 * so instances can scale, restart, or move without breaking routing.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
