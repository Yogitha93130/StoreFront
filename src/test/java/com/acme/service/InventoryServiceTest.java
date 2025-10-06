package com.acme.service;

import com.acme.model.Inventory;
import com.acme.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    void getLowStockItems_WhenItemsBelowThreshold_ShouldReturnLowStockList() {
        // Arrange
        Inventory lowStock1 = new Inventory("store-1", "prod-123", 3, 5); // below min
        Inventory lowStock2 = new Inventory("store-1", "prod-456", 2, 5); // below min
        Inventory adequateStock = new Inventory("store-1", "prod-789", 10, 5); // above min
        
        List<Inventory> allInventory = Arrays.asList(lowStock1, lowStock2, adequateStock);
        when(inventoryRepository.findByStoreId("store-1")).thenReturn(allInventory);
        
        // Act
        List<Inventory> result = inventoryService.getLowStockItems("store-1");
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(lowStock1));
        assertTrue(result.contains(lowStock2));
        assertFalse(result.contains(adequateStock));
        
        verify(inventoryRepository, times(1)).findByStoreId("store-1");
    }

    @Test
    void updateInventory_WithValidQuantity_ShouldUpdateStock() {
        // Arrange
        Inventory existingInventory = new Inventory("store-1", "prod-123", 10, 5);
        when(inventoryRepository.findByStoreIdAndProductId("store-1", "prod-123"))
            .thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> 
            invocation.getArgument(0));
        
        // Act
        Inventory result = inventoryService.updateInventory("store-1", "prod-123", 15);
        
        // Assert
        assertNotNull(result);
        assertEquals(15, result.getQuantity());
        assertEquals("store-1", result.getStoreId());
        assertEquals("prod-123", result.getProductId());
        
        verify(inventoryRepository, times(1)).findByStoreIdAndProductId("store-1", "prod-123");
        verify(inventoryRepository, times(1)).save(existingInventory);
    }

    @Test
    void canFulfillOrder_WithSufficientInventory_ShouldReturnTrue() {
        // Arrange
        when(inventoryRepository.findByStoreIdAndProductId("store-1", "prod-123"))
            .thenReturn(Optional.of(new Inventory("store-1", "prod-123", 10, 5)));
        when(inventoryRepository.findByStoreIdAndProductId("store-1", "prod-456"))
            .thenReturn(Optional.of(new Inventory("store-1", "prod-456", 3, 5)));
        
        // Act & Assert
        assertTrue(inventoryService.canFulfillOrder("store-1", "prod-123", 5));
        assertTrue(inventoryService.canFulfillOrder("store-1", "prod-456", 3));
        assertFalse(inventoryService.canFulfillOrder("store-1", "prod-456", 4)); // insufficient
    }
}
