package com.foodsupply.management.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class NutritionProgrammeRequest {

    @NotBlank(message = "Programme name is required")
    @Size(min = 2, max = 150, message = "Programme name must be between 2 and 150 characters")
    private String programmeName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 100, message = "Target group must not exceed 100 characters")
    private String targetGroup;

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
}
