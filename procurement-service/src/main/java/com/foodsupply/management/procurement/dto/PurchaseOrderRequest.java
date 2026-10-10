package com.foodsupply.management.procurement.dto;
import java.time.LocalDate;
public record PurchaseOrderRequest(Long supplierId,String status,LocalDate orderDate,LocalDate expectedDelivery) {}
