package com.acme.model;

public class InventoryAlert {
    private String productId;
    private int threshold;
    
    public InventoryAlert(String productId, int threshold) {
        this.productId = productId;
        this.threshold = threshold;
    }
    
    // Getters
    public String getProductId() { return productId; }
    public int getThreshold() { return threshold; }
}
