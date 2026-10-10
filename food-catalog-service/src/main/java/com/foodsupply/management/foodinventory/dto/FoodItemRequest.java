package com.foodsupply.management.foodinventory.dto;
import java.math.BigDecimal;
public record FoodItemRequest(String name,String category,String unit,BigDecimal caloriesPerUnit,BigDecimal proteinPerUnit) {}
