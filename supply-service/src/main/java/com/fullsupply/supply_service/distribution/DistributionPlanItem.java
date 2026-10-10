package com.fullsupply.supply_service.distribution;

import com.fullsupply.supply_service.catalog.FoodItem;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "distribution_plan_items")
public class DistributionPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_item_id")
    private Long planItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private DistributionPlan plan;

    @Column(name = "beneficiary_id", nullable = false)
    private Long beneficiaryId; // owned by beneficiary-service

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "planned_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal plannedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_id", nullable = false)
    private DistributionCentre centre;

    public DistributionPlanItem() {}

    public Long getPlanItemId() { return planItemId; }
    public void setPlanItemId(Long planItemId) { this.planItemId = planItemId; }

    public DistributionPlan getPlan() { return plan; }
    public void setPlan(DistributionPlan plan) { this.plan = plan; }

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public BigDecimal getPlannedQuantity() { return plannedQuantity; }
    public void setPlannedQuantity(BigDecimal plannedQuantity) { this.plannedQuantity = plannedQuantity; }

    public DistributionCentre getCentre() { return centre; }
    public void setCentre(DistributionCentre centre) { this.centre = centre; }
}