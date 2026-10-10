package com.fullsupply.supply_service.procurement;

import com.fullsupply.supply_service.inventory.BatchService;
import com.fullsupply.supply_service.inventory.StockBatch;
import com.fullsupply.supply_service.inventory.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProcurementService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository orderItemRepository;
    private final ProcurementRequestRepository requestRepository;
    private final BatchService batchService;

    public ProcurementService(PurchaseOrderRepository purchaseOrderRepository,
                              PurchaseOrderItemRepository orderItemRepository,
                              ProcurementRequestRepository requestRepository,
                              BatchService batchService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.requestRepository = requestRepository;
        this.batchService = batchService;
    }


    @Transactional
    public ProcurementRequest createRequest(ProcurementRequest request) {
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");
        return requestRepository.save(request);
    }

    @Transactional
    public void approveRequestAndPlaceOrder(Long requestId) {
        ProcurementRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));
        request.setStatus("APPROVED");
        requestRepository.save(request);
    }


    @Transactional
    public void markDelivered(Long purchaseOrderId, Long warehouseId, LocalDate expiryDateForAllItems) {
        PurchaseOrder order = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found: " + purchaseOrderId));

        List<PurchaseOrderItem> items = orderItemRepository.findByPurchaseOrder_PurchaseOrderId(purchaseOrderId);

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(warehouseId);

        for (PurchaseOrderItem item : items) {
            StockBatch batch = new StockBatch();
            batch.setFoodItem(item.getFoodItem());
            batch.setWarehouse(warehouse);
            batch.setBatchNumber("PO" + purchaseOrderId + "-ITEM" + item.getOrderItemId());
            batch.setQuantityAvailable(item.getQuantity());
            batch.setExpiryDate(expiryDateForAllItems);
            batch.setReceivedDate(LocalDate.now());
            batchService.receiveStock(batch);
        }

        order.setStatus("DELIVERED");
        purchaseOrderRepository.save(order);
    }
}