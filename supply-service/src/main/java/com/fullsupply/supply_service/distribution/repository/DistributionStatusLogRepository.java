package com.fullsupply.supply_service.distribution.repository;

import com.fullsupply.supply_service.distribution.entity.DistributionStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DistributionStatusLogRepository extends JpaRepository<DistributionStatusLog, Long> {
    List<DistributionStatusLog> findByPlanItem_PlanItemId(Long planItemId);
}