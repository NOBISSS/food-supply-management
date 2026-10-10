package com.fullsupply.supply_service.procurement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProcurementRequestRepository extends JpaRepository<ProcurementRequest, Long> {
    List<ProcurementRequest> findByStatus(String status);
}