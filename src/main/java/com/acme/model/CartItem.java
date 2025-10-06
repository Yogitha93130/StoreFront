package com.acme.model;

public class CartItem {
    private Product product;
    private int quantity;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    // Additional constructor for the test
    public CartItem(ShoppingCart cart, Product product, int quantity) {
        this(product, quantity);
    }
    
    public double calculateSubtotal() {
        return product.getBasePrice() * quantity;
    }
    
    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
