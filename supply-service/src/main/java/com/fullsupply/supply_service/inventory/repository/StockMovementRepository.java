package com.fullsupply.supply_service.inventory.repository;

import com.fullsupply.supply_service.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByBatch_BatchId(Long batchId);
}