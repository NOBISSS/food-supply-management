package com.fullsupply.supply_service.inventory.repository;

import com.fullsupply.supply_service.inventory.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {
    List<StockBatch> findByFoodItem_FoodItemIdOrderByExpiryDateAsc(Long foodItemId);
    List<StockBatch> findByWarehouse_WarehouseId(Long warehouseId);
}