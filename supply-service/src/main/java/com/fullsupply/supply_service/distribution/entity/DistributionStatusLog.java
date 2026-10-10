package com.fullsupply.supply_service.distribution.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "distribution_status_log")
public class DistributionStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_log_id")
    private Long statusLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_item_id", nullable = false)
    private DistributionPlanItem planItem;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "updated_date", nullable = false)
    private LocalDate updatedDate;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "remarks", length = 255)
    private String remarks;

    public DistributionStatusLog() {}

    public Long getStatusLogId() { return statusLogId; }
    public void setStatusLogId(Long statusLogId) { this.statusLogId = statusLogId; }

    public DistributionPlanItem getPlanItem() { return planItem; }
    public void setPlanItem(DistributionPlanItem planItem) { this.planItem = planItem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDate updatedDate) { this.updatedDate = updatedDate; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}