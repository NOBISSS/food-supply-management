package com.fullsupply.supply_service.inventory.repository;

import com.fullsupply.supply_service.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}