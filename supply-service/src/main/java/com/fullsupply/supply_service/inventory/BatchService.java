package com.fullsupply.supply_service.inventory;

import com.fullsupply.supply_service.inventory.entity.StockBatch;
import com.fullsupply.supply_service.inventory.entity.StockMovement;
import com.fullsupply.supply_service.inventory.repository.StockBatchRepository;
import com.fullsupply.supply_service.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BatchService {

    private final StockBatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    public BatchService(StockBatchRepository batchRepository, StockMovementRepository movementRepository) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }


    @Transactional
    public StockBatch receiveStock(StockBatch batch) {
        StockBatch saved = batchRepository.save(batch);

        StockMovement movement = new StockMovement();
        movement.setBatch(saved);
        movement.setMovementType(StockMovement.MovementType.IN);
        movement.setQuantity(saved.getQuantityAvailable());
        movement.setMovementDate(LocalDate.now());
        movement.setRemarks("Stock received");
        movementRepository.save(movement);

        return saved;
    }


    @Transactional
    public void deductStockFefo(Long foodItemId, BigDecimal requestedQuantity, Long referenceId, String remarks) {
        List<StockBatch> batches = batchRepository.findByFoodItem_FoodItemIdOrderByExpiryDateAsc(foodItemId);

        BigDecimal remaining = requestedQuantity;

        for (StockBatch batch : batches) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal available = batch.getQuantityAvailable();
            if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal deduction = available.min(remaining);

            batch.setQuantityAvailable(available.subtract(deduction));
            batchRepository.save(batch);

            StockMovement movement = new StockMovement();
            movement.setBatch(batch);
            movement.setMovementType(StockMovement.MovementType.OUT);
            movement.setQuantity(deduction);
            movement.setMovementDate(LocalDate.now());
            movement.setReferenceId(referenceId);
            movement.setRemarks(remarks);
            movementRepository.save(movement);

            remaining = remaining.subtract(deduction);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                    "Insufficient stock for food item " + foodItemId +
                            ": short by " + remaining + " units"
            );
        }
    }


    public BigDecimal getTotalAvailable(Long foodItemId) {
        return batchRepository.findByFoodItem_FoodItemIdOrderByExpiryDateAsc(foodItemId).stream()
                .map(StockBatch::getQuantityAvailable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}