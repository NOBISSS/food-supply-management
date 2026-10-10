package com.foodsupply.qualityreport.quality.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "quality_corrective_actions")
public class QualityCorrectiveAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    @Column(name = "wastage_id", nullable = false)
    private Long wastageId;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "action_date")
    private LocalDate actionDate;

    @Column(name = "responsible_person")
    private String responsiblePerson;

    @Column(name = "status")
    private String status;

    public QualityCorrectiveAction() {}

    public Long getActionId() { return actionId; }
    public void setActionId(Long actionId) { this.actionId = actionId; }

    public Long getWastageId() { return wastageId; }
    public void setWastageId(Long wastageId) { this.wastageId = wastageId; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public LocalDate getActionDate() { return actionDate; }
    public void setActionDate(LocalDate actionDate) { this.actionDate = actionDate; }

    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
