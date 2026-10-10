package com.fullsupply.supply_service.catalog.repository;

import com.fullsupply.supply_service.catalog.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem>
    findByCategory_CategoryId
            (Long categoryId);
}