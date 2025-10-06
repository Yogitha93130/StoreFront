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
import static org.mockito.ArgumentMatchers.any;
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
    private Store sampleStore;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartService(productRepository, inventoryRepository);
        shoppingCart = new ShoppingCart();
        shoppingCart.setCartId("cart-123");
        
        sampleStore = new Store();
        sampleStore.setStoreId("store-1");
        sampleStore.setName("ACME Downtown");
        
        sampleProduct = new Product();
        sampleProduct.setProductId("prod-123");
        sampleProduct.setName("Nike Air Max");
        sampleProduct.setBasePrice(129.99);
        sampleProduct.setSize("10");
        sampleProduct.setColor("Black");
    }

    @Test
    void addItemToCart_WhenProductExistsAndInStock_ShouldAddItem() {
        // Arrange
        when(productRepository.findById("prod-123")).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.findByStoreIdAndProductId("store-1", "prod-123"))
            .thenReturn(Optional.of(new Inventory("store-1", "prod-123", 10, 5)));
        
        // Act
        CartItem result = shoppingCartService.addItemToCart(
            shoppingCart, "prod-123", "store-1", 2);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals(sampleProduct, result.getProduct());
        assertEquals(258.00, result.calculateSubtotal(), 0.01);
        
        // Verify mock interactions
        verify(productRepository, times(1)).findById("prod-123");
        verify(inventoryRepository, times(1)).findByStoreIdAndProductId("store-1", "prod-123");
    }

    @Test
    void addItemToCart_WhenProductOutOfStock_ShouldThrowException() {
        // Arrange
        when(productRepository.findById("prod-123")).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.findByStoreIdAndProductId("store-1", "prod-123"))
            .thenReturn(Optional.of(new Inventory("store-1", "prod-123", 1, 5)));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> shoppingCartService.addItemToCart(shoppingCart, "prod-123", "store-1", 5));
        
        assertEquals("Insufficient stock. Only 1 items available.", exception.getMessage());
        verify(inventoryRepository, times(1)).findByStoreIdAndProductId("store-1", "prod-123");
    }

    @Test
    void calculateCartTotal_WithMultipleItemsAndPromotion_ShouldApplyDiscount() {
        // Arrange
        Product product2 = new Product();
        product2.setProductId("prod-456");
        product2.setName("Adidas Ultraboost");
        product2.setBasePrice(179.99);
        
        Promotion promotion = new Promotion();
        promotion.setPromotionId("promo-1");
        promotion.setDiscountPercent(10.0);
        
        CartItem item1 = new CartItem(shoppingCart, sampleProduct, 1);
        CartItem item2 = new CartItem(shoppingCart, product2, 1);
        shoppingCart.getCartItems().add(item1);
        shoppingCart.getCartItems().add(item2);
        shoppingCart.setAppliedPromotion(promotion);
        
        // Act
        double total = shoppingCartService.calculateCartTotal(shoppingCart);
        
        // Assert
        double expectedSubtotal = 129.99 + 179.99; // 309.98
        double expectedDiscount = 309.98 * 0.10;   // 30.998
        double expectedTotal = expectedSubtotal - expectedDiscount; // 278.982
        
        assertEquals(expectedTotal, total, 0.01);
    }
}
