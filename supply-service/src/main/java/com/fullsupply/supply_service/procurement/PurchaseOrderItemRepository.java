package com.fullsupply.supply_service.procurement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findByPurchaseOrder_PurchaseOrderId(Long purchaseOrderId);
}