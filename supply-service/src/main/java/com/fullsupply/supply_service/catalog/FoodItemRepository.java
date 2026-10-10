package com.fullsupply.supply_service.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByCategory_CategoryId(Long categoryId);
}