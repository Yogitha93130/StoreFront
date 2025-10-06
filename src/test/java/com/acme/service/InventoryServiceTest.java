package com.acme;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @Test
    void checkLowStock_ShouldSendNotification() {
        // Arrange
        InventoryService inventoryService = new InventoryService(inventoryRepository, notificationService);
        
        // Act
        inventoryService.checkLowStock();
        
        // Assert
        verify(notificationService).sendLowStockAlert("test-product");
    }
    
    @Test
    void updateStockLevel_ShouldUpdateRepository() {
        // Arrange
        InventoryService inventoryService = new InventoryService(inventoryRepository, notificationService);
        when(inventoryRepository.getCurrentStock("prod-123")).thenReturn(5);
        
        // Act
        inventoryService.updateStockLevel("prod-123", 10);
        
        // Assert
        verify(inventoryRepository).updateStock("prod-123", 10);
        verify(notificationService).sendStockUpdateNotification("prod-123", 5, 10);
    }
}
