package com.foodsupply.management.foodinventory.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="food_items")
public class FoodItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=50) private String category;
 @Column(nullable=false,length=20) private String unit;
 @Column(precision=10,scale=2) private BigDecimal caloriesPerUnit;
 @Column(precision=10,scale=2) private BigDecimal proteinPerUnit;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;} public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
 public BigDecimal getCaloriesPerUnit(){return caloriesPerUnit;} public void setCaloriesPerUnit(BigDecimal v){caloriesPerUnit=v;}
 public BigDecimal getProteinPerUnit(){return proteinPerUnit;} public void setProteinPerUnit(BigDecimal v){proteinPerUnit=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
