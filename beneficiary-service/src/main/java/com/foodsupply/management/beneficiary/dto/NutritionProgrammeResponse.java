package com.foodsupply.management.beneficiary.dto;

import java.time.LocalDate;

public class NutritionProgrammeResponse {
    private Long id;
    private String programmeName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String targetGroup;
    private String status;

    public NutritionProgrammeResponse(Long id, String programmeName, String description,
                                       LocalDate startDate, LocalDate endDate,
                                       String targetGroup, String status) {
        this.id = id;
        this.programmeName = programmeName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetGroup = targetGroup;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getProgrammeName() { return programmeName; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getTargetGroup() { return targetGroup; }
    public String getStatus() { return status; }
}
