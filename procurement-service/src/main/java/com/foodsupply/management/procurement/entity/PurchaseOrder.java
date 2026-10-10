package com.foodsupply.management.procurement.entity;
import jakarta.persistence.*;import java.time.LocalDate;import java.time.LocalDateTime;
@Entity @Table(name="purchase_orders") public class PurchaseOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;@Column(nullable=false) private Long supplierId;
 @Column(nullable=false,length=20) private String status="REQUESTED";@Column(nullable=false) private LocalDate orderDate=LocalDate.now();private LocalDate expectedDelivery;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Long getId(){return id;}public Long getSupplierId(){return supplierId;}public void setSupplierId(Long v){supplierId=v;}public String getStatus(){return status;}public void setStatus(String v){status=v;}public LocalDate getOrderDate(){return orderDate;}public void setOrderDate(LocalDate v){orderDate=v;}public LocalDate getExpectedDelivery(){return expectedDelivery;}public void setExpectedDelivery(LocalDate v){expectedDelivery=v;}public LocalDateTime getCreatedAt(){return createdAt;}}
