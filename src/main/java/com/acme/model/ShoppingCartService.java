package com.acme.service;

import com.acme.model.*;
import com.acme.repository.InventoryRepository;
import com.acme.repository.ProductRepository;

public class ShoppingCartService {
    private ProductRepository productRepository;
    private InventoryRepository inventoryRepository;
    
    public ShoppingCartService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }
    
    public CartItem addItemToCart(ShoppingCart cart, String productId, String storeId, int quantity) {
        // This would contain the actual business logic
        // For now, return a simple implementation for testing
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            
        int stockLevel = inventoryRepository.getStockLevel(storeId, productId);
        if (stockLevel < quantity) {
            throw new IllegalArgumentException(
                "Cannot add " + quantity + " items. Only " + stockLevel + " available in stock.");
        }
        
        CartItem item = new CartItem(product, quantity);
        cart.addItem(item);
        return item;
    }
    
    public double calculateCartTotal(ShoppingCart cart) {
        return cart.getCartItems().stream()
            .mapToDouble(CartItem::calculateSubtotal)
            .sum();
    }
}
