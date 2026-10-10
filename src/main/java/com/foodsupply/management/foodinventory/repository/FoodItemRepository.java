package com.foodsupply.management.foodinventory.repository;
import com.foodsupply.management.foodinventory.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FoodItemRepository extends JpaRepository<FoodItem,Long> {}
