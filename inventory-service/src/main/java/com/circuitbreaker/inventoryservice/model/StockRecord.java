package com.circuitbreaker.inventoryservice.model;

public class StockRecord {

    private String productId;
    private int quantityAvailable;
    private String warehouse;

    public StockRecord() {
    }

    public StockRecord(String productId, int quantityAvailable, String warehouse) {
        this.productId = productId;
        this.quantityAvailable = quantityAvailable;
        this.warehouse = warehouse;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
}
