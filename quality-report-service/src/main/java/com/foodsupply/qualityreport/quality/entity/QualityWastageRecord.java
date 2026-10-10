package com.foodsupply.qualityreport.quality.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quality_wastage_records")
public class QualityWastageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wastage_id")
    private Long wastageId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "wastage_quantity", nullable = false)
    private BigDecimal wastageQuantity;

    @Column(name = "wastage_reason")
    private String wastageReason;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @Column(name = "recorded_by")
    private String recordedBy;

    public QualityWastageRecord() {}

    public Long getWastageId() { return wastageId; }
    public void setWastageId(Long wastageId) { this.wastageId = wastageId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public BigDecimal getWastageQuantity() { return wastageQuantity; }
    public void setWastageQuantity(BigDecimal wastageQuantity) { this.wastageQuantity = wastageQuantity; }

    public String getWastageReason() { return wastageReason; }
    public void setWastageReason(String wastageReason) { this.wastageReason = wastageReason; }

    public LocalDate getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }
}
