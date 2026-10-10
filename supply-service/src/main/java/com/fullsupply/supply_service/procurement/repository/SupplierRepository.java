package com.fullsupply.supply_service.procurement.repository;

import com.fullsupply.supply_service.procurement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}