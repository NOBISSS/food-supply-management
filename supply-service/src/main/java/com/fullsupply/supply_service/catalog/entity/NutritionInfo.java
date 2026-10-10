package com.fullsupply.supply_service.catalog.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalog_nutrition_values")
public class NutritionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrition_value_id")
    private Long nutritionValueId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "calories", precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein", precision = 10, scale = 2)
    private BigDecimal protein;

    @Column(name = "carbs", precision = 10, scale = 2)
    private BigDecimal carbs;

    @Column(name = "fat", precision = 10, scale = 2)
    private BigDecimal fat;

    @Column(name = "vitamins", length = 150)
    private String vitamins;

    public NutritionInfo() {}

    public Long getNutritionValueId() { return nutritionValueId; }
    public void setNutritionValueId(Long nutritionValueId) {
        this.nutritionValueId = nutritionValueId;
    }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public BigDecimal getCalories() {
        return calories;
    }
    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getCarbs() {
        return carbs;
    }
    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }

    public BigDecimal getFat() {
        return fat;
    }
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public String getVitamins() {
        return vitamins;
    }
    public void setVitamins(String vitamins) {
        this.vitamins = vitamins;
    }
}