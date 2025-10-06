package com.acme.model;

public class Product {
    private String productId;
    private String name;
    private double basePrice;
    private String size;
    private String color;
    
    // Constructors
    public Product() {}
    
    public Product(String productId, String name, double basePrice, String size, String color) {
        this.productId = productId;
        this.name = name;
        this.basePrice = basePrice;
        this.size = size;
        this.color = color;
    }
    
    // Getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
