package com.fullsupply.supply_service.controller;

import com.fullsupply.supply_service.catalog.entity.FoodItem;
import com.fullsupply.supply_service.inventory.BatchService;
import com.fullsupply.supply_service.inventory.entity.StockBatch;
import com.fullsupply.supply_service.inventory.entity.Warehouse;
import com.fullsupply.supply_service.inventory.repository.StockBatchRepository;
import com.fullsupply.supply_service.inventory.repository.WarehouseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final BatchService batchService;
    private final StockBatchRepository batchRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryController(BatchService batchService,
                               StockBatchRepository batchRepository,
                               WarehouseRepository warehouseRepository) {
        this.batchService = batchService;
        this.batchRepository = batchRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping("/warehouses")
    public List<Warehouse> getWarehouses() {
        return warehouseRepository.findAll();
    }

    @GetMapping("/batches")
    public List<StockBatch> getAllBatches() {
        return batchRepository.findAll();
    }

    @GetMapping("/batches/by-item/{foodItemId}")
    public List<StockBatch> getBatchesByItem(@PathVariable Long foodItemId) {
        return batchRepository.findByFoodItem_FoodItemIdOrderByExpiryDateAsc(foodItemId);
    }

    @GetMapping("/available/{foodItemId}")
    public ResponseEntity<BigDecimal> getTotalAvailable(@PathVariable Long foodItemId) {
        return ResponseEntity.ok(batchService.getTotalAvailable(foodItemId));
    }

    public record ReceiveStockRequest(
            Long foodItemId, Long warehouseId, String batchNumber,
            BigDecimal quantityAvailable, LocalDate expiryDate
    ) {}

    @PostMapping("/receive")
    public ResponseEntity<StockBatch> receiveStock(@RequestBody ReceiveStockRequest request) {
        StockBatch batch = new StockBatch();
        var foodItem = new FoodItem();
        foodItem.setFoodItemId(request.foodItemId());
        var warehouse = new Warehouse();
        warehouse.setWarehouseId(request.warehouseId());

        batch.setFoodItem(foodItem);
        batch.setWarehouse(warehouse);
        batch.setBatchNumber(request.batchNumber());
        batch.setQuantityAvailable(request.quantityAvailable());
        batch.setExpiryDate(request.expiryDate());
        batch.setReceivedDate(LocalDate.now());

        return ResponseEntity.ok(batchService.receiveStock(batch));
    }

    public record DeductStockRequest(Long foodItemId, BigDecimal quantity, Long referenceId, String remarks) {}

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@RequestBody DeductStockRequest request) {
        batchService.deductStockFefo(request.foodItemId(), request.quantity(), request.referenceId(), request.remarks());
        return ResponseEntity.ok("Deducted successfully");
    }
}