package com.fullsupply.supply_service.catalog.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalog_food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_item_id")
    private Long foodItemId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private FoodCategory category;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "recommended_quantity", precision = 10, scale = 2)
    private BigDecimal recommendedQuantity;

    @Column(name = "description", length = 255)
    private String description;

    public FoodItem() {}

    public Long getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getRecommendedQuantity() { return recommendedQuantity; }
    public void setRecommendedQuantity(BigDecimal recommendedQuantity)
    {
        this.recommendedQuantity = recommendedQuantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
}