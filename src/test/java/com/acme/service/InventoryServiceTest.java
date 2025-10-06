package com.acme.service;

import com.acme.model.InventoryAlert;
import com.acme.repository.InventoryRepository;
import com.acme.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    
    @Mock
    private NotificationService notificationService;
    
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryRepository, notificationService);
    }

    @Test
    void checkLowStock_WhenItemsBelowThreshold_ShouldSendNotifications() {
        // Arrange
        List<String> lowStockProducts = Arrays.asList("prod-123", "prod-456");
        when(inventoryRepository.findLowStockItems(5)).thenReturn(lowStockProducts);
        
        // Act
        List<InventoryAlert> alerts = inventoryService.checkLowStock();
        
        // Assert
        assertEquals(2, alerts.size());
        assertTrue(alerts.stream().anyMatch(alert -> alert.getProductId().equals("prod-123")));
        assertTrue(alerts.stream().anyMatch(alert -> alert.getProductId().equals("prod-456")));
        
        // Verify notification service was called for each low stock item
        verify(notificationService, times(2)).sendLowStockAlert(any(InventoryAlert.class));
        verify(inventoryRepository).findLowStockItems(5);
    }

    @Test
    void updateStockLevel_WithValidQuantity_ShouldUpdateAndNotifyIfLow() {
        // Arrange
        when(inventoryRepository.getCurrentStock("prod-123")).thenReturn(2);
        
        // Act
        inventoryService.updateStockLevel("prod-123", 10);
        
        // Assert
        verify(inventoryRepository).updateStock("prod-123", 10);
        // Since previous stock was 2 (low), should notify about stock increase
        verify(notificationService).sendStockUpdateNotification("prod-123", 2, 10);
    }
}
