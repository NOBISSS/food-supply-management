package com.fullsupply.supply_service.distribution.repository;

import com.fullsupply.supply_service.distribution.entity.DistributionPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DistributionPlanItemRepository extends JpaRepository<DistributionPlanItem, Long> {
    List<DistributionPlanItem> findByPlan_PlanId(Long planId);
}