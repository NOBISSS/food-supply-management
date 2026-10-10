package com.fullsupply.supply_service.catalog.repository;

import com.fullsupply.supply_service.catalog.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
}