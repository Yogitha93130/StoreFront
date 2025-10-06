package com.acme.model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private String cartId;
    private List<CartItem> cartItems;
    
    public ShoppingCart(String cartId) {
        this.cartId = cartId;
        this.cartItems = new ArrayList<>();
    }
    
    // Default constructor
    public ShoppingCart() {
        this.cartItems = new ArrayList<>();
    }
    
    // Getters and setters
    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
    
    public void addItem(CartItem item) { 
        this.cartItems.add(item); 
    }
}
