package com.circuitbreaker.inventoryservice.controller;

import com.circuitbreaker.inventoryservice.model.StockRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    // In-memory stock levels, keyed to the same product IDs used by product-service.
    private static final List<StockRecord> STOCK = List.of(
            new StockRecord("P001", 142, "WH-NORTH"),
            new StockRecord("P002", 58, "WH-NORTH"),
            new StockRecord("P003", 0, "WH-SOUTH"),
            new StockRecord("P004", 310, "WH-SOUTH"),
            new StockRecord("P005", 76, "WH-NORTH")
    );

    @Value("${server.port}")
    private String port;

    @GetMapping
    public Map<String, Object> getAllStock() {
        return Map.of(
                "servedBy", "inventory-service:" + port,
                "stock", STOCK
        );
    }

    @GetMapping("/{productId}")
    public StockRecord getStock(@PathVariable String productId) {
        return STOCK.stream()
                .filter(s -> s.getProductId().equalsIgnoreCase(productId))
                .findFirst()
                .orElse(new StockRecord(productId, 0, "UNKNOWN"));
    }
}
