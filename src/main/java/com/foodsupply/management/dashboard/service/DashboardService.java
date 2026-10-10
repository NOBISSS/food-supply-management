package com.foodsupply.management.dashboard.service;

import com.foodsupply.management.beneficiary.repository.BeneficiaryRepository;
import com.foodsupply.management.distribution.repository.DistributionPlanRepository;
import com.foodsupply.management.foodinventory.repository.FoodItemRepository;
import com.foodsupply.management.procurement.repository.PurchaseOrderRepository;
import com.foodsupply.management.quality.repository.QualityInspectionRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DashboardService {
    private final BeneficiaryRepository beneficiaries;
    private final FoodItemRepository foodItems;
    private final PurchaseOrderRepository purchaseOrders;
    private final DistributionPlanRepository distributionPlans;
    private final QualityInspectionRepository inspections;

    public DashboardService(BeneficiaryRepository beneficiaries, FoodItemRepository foodItems,
                            PurchaseOrderRepository purchaseOrders, DistributionPlanRepository distributionPlans,
                            QualityInspectionRepository inspections) {
        this.beneficiaries = beneficiaries;
        this.foodItems = foodItems;
        this.purchaseOrders = purchaseOrders;
        this.distributionPlans = distributionPlans;
        this.inspections = inspections;
    }

    public Map<String, Long> getSummary() {
        return Map.of("beneficiaries", beneficiaries.count(), "foodItems", foodItems.count(),
                "purchaseOrders", purchaseOrders.count(), "distributionPlans", distributionPlans.count(),
                "qualityInspections", inspections.count());
    }
}
