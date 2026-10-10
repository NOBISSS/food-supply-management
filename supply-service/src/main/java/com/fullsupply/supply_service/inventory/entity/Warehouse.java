package com.fullsupply.supply_service.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "inventory_warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "capacity", precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(name = "minimum_stock_threshold", precision = 10, scale = 2)
    private BigDecimal minimumStockThreshold;

    public Warehouse() {}

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getCapacity() { return capacity; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }

    public BigDecimal getMinimumStockThreshold() { return minimumStockThreshold; }
    public void setMinimumStockThreshold(BigDecimal minimumStockThreshold) { this.minimumStockThreshold = minimumStockThreshold; }
}