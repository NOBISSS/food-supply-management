package com.fullsupply.supply_service.procurement.entity;

import com.fullsupply.supply_service.catalog.entity.FoodItem;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "procurement_requests")
public class ProcurementRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "requested_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal requestedQuantity;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "requested_by", length = 100)
    private String requestedBy;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    public ProcurementRequest() {}

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public BigDecimal getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(BigDecimal requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}