package com.fullsupply.supply_service.distribution.repository;

import com.fullsupply.supply_service.distribution.entity.DistributionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributionPlanRepository extends JpaRepository<DistributionPlan, Long> {
}