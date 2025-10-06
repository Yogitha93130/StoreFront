package com.acme.service;

import com.acme.model.InventoryAlert;
import com.acme.repository.InventoryRepository;
import com.acme.notification.NotificationService;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryService {
    private InventoryRepository inventoryRepository;
    private NotificationService notificationService;
    
    public InventoryService(InventoryRepository inventoryRepository, NotificationService notificationService) {
        this.inventoryRepository = inventoryRepository;
        this.notificationService = notificationService;
    }
    
    public List<InventoryAlert> checkLowStock() {
        List<String> lowStockItems = inventoryRepository.findLowStockItems(5);
        
        return lowStockItems.stream()
            .map(productId -> {
                InventoryAlert alert = new InventoryAlert(productId, 5);
                notificationService.sendLowStockAlert(alert);
                return alert;
            })
            .collect(Collectors.toList());
    }
    
    public void updateStockLevel(String productId, int newQuantity) {
        int oldQuantity = inventoryRepository.getCurrentStock(productId);
        inventoryRepository.updateStock(productId, newQuantity);
        
        if (oldQuantity <= 5 && newQuantity > 5) {
            notificationService.sendStockUpdateNotification(productId, oldQuantity, newQuantity);
        }
    }
}
