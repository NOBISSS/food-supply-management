package com.fullsupply.supply_service.catalog.repository;

import com.fullsupply.supply_service.catalog.entity.NutritionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NutritionInfoRepository extends JpaRepository<NutritionInfo, Long> {
    Optional<NutritionInfo> findByFoodItem_FoodItemId(Long foodItemId);
}