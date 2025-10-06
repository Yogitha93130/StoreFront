package com.acme.service;

import com.acme.model.*;
import com.acme.repository.InventoryRepository;
import com.acme.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    private ShoppingCartService shoppingCartService;
    private ShoppingCart shoppingCart;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartService(productRepository, inventoryRepository);
        shoppingCart = new ShoppingCart("cart-123");
        
        sampleProduct = new Product("prod-123", "Nike Air Max", 129.99, "10", "Black");
    }

    @Test
    void addItemToCart_WhenProductExistsAndInStock_ShouldAddItemSuccessfully() {
        // Arrange - Mock the dependencies
        when(productRepository.findById("prod-123")).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.getStockLevel("store-1", "prod-123")).thenReturn(10);
        
        // Act
        CartItem result = shoppingCartService.addItemToCart(
            shoppingCart, "prod-123", "store-1", 2);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals(sampleProduct, result.getProduct());
        assertEquals(259.98, result.calculateSubtotal(), 0.01);
        
        // Verify mock interactions
        verify(productRepository).findById("prod-123");
        verify(inventoryRepository).getStockLevel("store-1", "prod-123");
    }

    @Test
    void addItemToCart_WhenInsufficientStock_ShouldThrowException() {
        // Arrange
        when(productRepository.findById("prod-123")).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.getStockLevel("store-1", "prod-123")).thenReturn(1);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> shoppingCartService.addItemToCart(shoppingCart, "prod-123", "store-1", 3));
        
        assertEquals("Cannot add 3 items. Only 1 available in stock.", exception.getMessage());
        verify(inventoryRepository).getStockLevel("store-1", "prod-123");
    }

    @Test
    void calculateCartTotal_WithEmptyCart_ShouldReturnZero() {
        // Act
        double total = shoppingCartService.calculateCartTotal(shoppingCart);
        
        // Assert
        assertEquals(0.0, total, 0.01);
    }
}
