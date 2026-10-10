package com.foodsupply.management.beneficiary.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "nutrition_programmes")
public class NutritionProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "programme_id")
    private Long id;

    @Column(name = "programme_name", nullable = false, length = 150)
    private String programmeName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "target_group", length = 100)
    private String targetGroup;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "ACTIVE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProgrammeName() { return programmeName; }
    public void setProgrammeName(String programmeName) { this.programmeName = programmeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getTargetGroup() { return targetGroup; }
    public void setTargetGroup(String targetGroup) { this.targetGroup = targetGroup; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
