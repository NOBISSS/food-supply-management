package com.foodsupply.management.quality.dto;
import java.math.BigDecimal;import java.time.LocalDate;
public record QualityInspectionRequest(Long stockBatchId,LocalDate inspectionDate,String result,BigDecimal wastageQuantity,String correctiveAction,String inspectedBy) {}
