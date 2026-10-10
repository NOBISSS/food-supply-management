package com.fullsupply.supply_service.distribution.repository;

import com.fullsupply.supply_service.distribution.entity.DistributionCentre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributionCentreRepository extends JpaRepository<DistributionCentre, Long> {
}