package com.foodsupply.management.distribution.dto;
import java.math.BigDecimal;import java.time.LocalDate;
public record DistributionPlanRequest(Long beneficiaryId,Long foodItemId,BigDecimal quantity,Long distributionCentreId,LocalDate plannedDate,String status) {}
