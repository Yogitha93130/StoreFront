package com.acme.notification;

import com.acme.model.InventoryAlert;

public interface NotificationService {
    void sendLowStockAlert(InventoryAlert alert);
    void sendStockUpdateNotification(String productId, int oldQuantity, int newQuantity);
}
