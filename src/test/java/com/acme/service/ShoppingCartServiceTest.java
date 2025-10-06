package com.acme;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    @Test
    void addItemToCart_WhenProductExistsAndInStock_ShouldAddItem() {
        // Arrange
        ShoppingCartService service = new ShoppingCartService(productRepository, inventoryRepository);
        ShoppingCart cart = new ShoppingCart("cart-123");
        Product product = new Product("prod-123", "Nike Air Max", 129.99);
        
        when(productRepository.findById("prod-123")).thenReturn(product);
        when(inventoryRepository.getStockLevel("store-1", "prod-123")).thenReturn(10);
        
        // Act
        CartItem result = service.addItemToCart(cart, "prod-123", "store-1", 2);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals(product, result.getProduct());
        assertEquals(259.98, result.calculateSubtotal(), 0.01);
        
        verify(productRepository).findById("prod-123");
        verify(inventoryRepository).getStockLevel("store-1", "prod-123");
    }
    
    @Test
    void addItemToCart_WhenInsufficientStock_ShouldThrowException() {
        // Arrange
        ShoppingCartService service = new ShoppingCartService(productRepository, inventoryRepository);
        ShoppingCart cart = new ShoppingCart("cart-123");
        Product product = new Product("prod-123", "Nike Air Max", 129.99);
        
        when(productRepository.findById("prod-123")).thenReturn(product);
        when(inventoryRepository.getStockLevel("store-1", "prod-123")).thenReturn(1);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.addItemToCart(cart, "prod-123", "store-1", 3));
        
        assertEquals("Cannot add 3 items. Only 1 available.", exception.getMessage());
    }
    
    @Test
    void calculateCartTotal_WithEmptyCart_ShouldReturnZero() {
        // Arrange
        ShoppingCartService service = new ShoppingCartService(productRepository, inventoryRepository);
        ShoppingCart cart = new ShoppingCart("cart-123");
        
        // Act
        double total = service.calculateCartTotal(cart);
        
        // Assert
        assertEquals(0.0, total, 0.01);
    }
}
