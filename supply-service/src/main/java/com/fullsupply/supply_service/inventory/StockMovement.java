package com.fullsupply.supply_service.inventory;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_stock_movements")
public class StockMovement {

    public enum MovementType { IN, OUT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private StockBatch batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 10)
    private MovementType movementType;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "remarks", length = 255)
    private String remarks;

    public StockMovement() {}

    public Long getMovementId() {
        return movementId;
    }
    public void setMovementId(Long movementId) {
        this.movementId = movementId;
    }

    public StockBatch getBatch() {
        return batch;
    }
    public void setBatch(StockBatch batch) {
        this.batch = batch;
    }

    public MovementType getMovementType() {
        return movementType;
    }
    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDate getMovementDate() {
        return movementDate;
    }
    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }

    public Long getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}