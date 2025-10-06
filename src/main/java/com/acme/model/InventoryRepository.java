package com.acme.repository;

import java.util.List;

public interface InventoryRepository {
    int getStockLevel(String storeId, String productId);
    List<String> findLowStockItems(int threshold);
    int getCurrentStock(String productId);
    void updateStock(String productId, int quantity);
}
