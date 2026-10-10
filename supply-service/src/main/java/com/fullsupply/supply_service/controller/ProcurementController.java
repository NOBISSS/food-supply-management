package com.fullsupply.supply_service.controller;

import com.fullsupply.supply_service.catalog.entity.FoodItem;
import com.fullsupply.supply_service.procurement.*;
import com.fullsupply.supply_service.procurement.entity.ProcurementRequest;
import com.fullsupply.supply_service.procurement.entity.PurchaseOrder;
import com.fullsupply.supply_service.procurement.entity.PurchaseOrderItem;
import com.fullsupply.supply_service.procurement.entity.Supplier;
import com.fullsupply.supply_service.procurement.repository.ProcurementRequestRepository;
import com.fullsupply.supply_service.procurement.repository.PurchaseOrderItemRepository;
import com.fullsupply.supply_service.procurement.repository.PurchaseOrderRepository;
import com.fullsupply.supply_service.procurement.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/procurement")
public class ProcurementController {

    private final ProcurementService procurementService;
    private final SupplierRepository supplierRepository;
    private final ProcurementRequestRepository requestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository orderItemRepository;

    public ProcurementController(ProcurementService procurementService,
                                 SupplierRepository supplierRepository,
                                 ProcurementRequestRepository requestRepository,
                                 PurchaseOrderRepository purchaseOrderRepository,
                                 PurchaseOrderItemRepository orderItemRepository) {
        this.procurementService = procurementService;
        this.supplierRepository = supplierRepository;
        this.requestRepository = requestRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/suppliers")
    public List<Supplier> getSuppliers() {
        return supplierRepository.findAll();
    }

    @PostMapping("/suppliers")
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public record CreateRequestBody(Long foodItemId,
                                    BigDecimal requestedQuantity,
                                    String requestedBy)
    {}

    @PostMapping("/requests")
    public ProcurementRequest createRequest(@RequestBody CreateRequestBody body) {
        ProcurementRequest request = new ProcurementRequest();
        FoodItem foodItem = new FoodItem();
        foodItem.setFoodItemId(body.foodItemId());
        request.setFoodItem(foodItem);
        request.setRequestedQuantity(body.requestedQuantity());
        request.setRequestedBy(body.requestedBy());
        return procurementService.createRequest(request);
    }

    @GetMapping("/requests")
    public List<ProcurementRequest> getRequests() {
        return requestRepository.findAll();
    }

    public record CreateOrderBody(Long supplierId,
                                  Long requestId,
                                  LocalDate expectedDeliveryDate,
                                  BigDecimal totalAmount) {}

    @PostMapping("/orders")
    public PurchaseOrder createOrder(@RequestBody CreateOrderBody body) {
        procurementService.approveRequestAndPlaceOrder(body.requestId());

        PurchaseOrder order = new PurchaseOrder();
        Supplier supplier = new Supplier();
        supplier.setSupplierId(body.supplierId());
        order.setSupplier(supplier);

        ProcurementRequest request = new ProcurementRequest();
        request.setRequestId(body.requestId());
        order.setRequest(request);

        order.setOrderDate(LocalDate.now());
        order.setExpectedDeliveryDate(body.expectedDeliveryDate());
        order.setTotalAmount(body.totalAmount());

        return purchaseOrderRepository.save(order);
    }

    public record AddOrderItemBody(Long foodItemId,
                                   BigDecimal quantity,
                                   BigDecimal unitPrice) {}

    @PostMapping("/orders/{orderId}/items")
    public PurchaseOrderItem addOrderItem(@PathVariable Long orderId, @RequestBody AddOrderItemBody body) {
        PurchaseOrderItem item = new PurchaseOrderItem();
        PurchaseOrder order = new PurchaseOrder();
        order.setPurchaseOrderId(orderId);
        item.setPurchaseOrder(order);

        FoodItem foodItem = new FoodItem();
        foodItem.setFoodItemId(body.foodItemId());
        item.setFoodItem(foodItem);

        item.setQuantity(body.quantity());
        item.setUnitPrice(body.unitPrice());

        return orderItemRepository.save(item);
    }

    public record MarkDeliveredBody(Long warehouseId, LocalDate expiryDate) {}

    @PostMapping("/orders/{orderId}/deliver")
    public ResponseEntity<String> markDelivered(@PathVariable Long orderId, @RequestBody MarkDeliveredBody body) {
        procurementService.markDelivered(orderId, body.warehouseId(), body.expiryDate());
        return ResponseEntity.ok("Order marked delivered and stock received");
    }

    @GetMapping("/orders")
    public List<PurchaseOrder> getOrders() {
        return purchaseOrderRepository.findAll();
    }
}