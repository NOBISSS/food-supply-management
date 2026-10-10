package com.fullsupply.supply_service.distribution.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "distribution_plans")
public class DistributionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "programme_id")
    private Long programmeId;

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Column(name = "target_beneficiary_count")
    private Integer targetBeneficiaryCount;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public DistributionPlan() {}

    public Long getPlanId() {
        return planId;
    }
    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getProgrammeId() {
        return programmeId;
    }
    public void setProgrammeId(Long programmeId) {
        this.programmeId = programmeId;
    }
    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public Integer getTargetBeneficiaryCount() {
        return targetBeneficiaryCount;
    }
    public void setTargetBeneficiaryCount(Integer targetBeneficiaryCount)
    {
        this.targetBeneficiaryCount = targetBeneficiaryCount;
    }

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
}