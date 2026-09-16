package com.circuitbreaker.productservice.controller;

import com.circuitbreaker.productservice.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // In-memory catalog. Fine for Week 1 scaffolding - swap for a real
    // repository/DB layer once the resilience patterns are in place.
    private static final List<Product> CATALOG = List.of(
            new Product("P001", "Wireless Earbuds Pro", "Electronics", 2499.00, true),
            new Product("P002", "Running Shoes v3", "Footwear", 3299.00, true),
            new Product("P003", "Smart Watch Lite", "Electronics", 4999.00, false),
            new Product("P004", "Ceramic Coffee Mug", "Home", 399.00, false),
            new Product("P005", "Yoga Mat XL", "Fitness", 899.00, true)
    );

    @Value("${server.port}")
    private String port;

    @GetMapping
    public Map<String, Object> getAllProducts() {
        return Map.of(
                "servedBy", "product-service:" + port,
                "count", CATALOG.size(),
                "products", CATALOG
        );
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return CATALOG.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/top-sellers")
    public Map<String, Object> getTopSellers() {
        List<Product> topSellers = CATALOG.stream()
                .filter(Product::isTopSeller)
                .collect(Collectors.toList());
        return Map.of(
                "servedBy", "product-service:" + port,
                "topSellers", topSellers
        );
    }
}
