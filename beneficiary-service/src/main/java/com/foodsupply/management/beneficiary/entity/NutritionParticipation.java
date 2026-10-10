package com.foodsupply.management.beneficiary.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "nutrition_participation")
public class NutritionParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long id;

    @Column(name = "programme_id", nullable = false)
    private Long programmeId;

    @Column(name = "beneficiary_id", nullable = false)
    private Long beneficiaryId;

    @Column(name = "enrolment_date", nullable = false)
    private LocalDate enrolmentDate;

    @Column(name = "participation_status", nullable = false, length = 20)
    private String participationStatus;

    @PrePersist
    protected void onCreate() {
        if (enrolmentDate == null) enrolmentDate = LocalDate.now();
        if (participationStatus == null) participationStatus = "ACTIVE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProgrammeId() { return programmeId; }
    public void setProgrammeId(Long programmeId) { this.programmeId = programmeId; }

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public LocalDate getEnrolmentDate() { return enrolmentDate; }
    public void setEnrolmentDate(LocalDate enrolmentDate) { this.enrolmentDate = enrolmentDate; }

    public String getParticipationStatus() { return participationStatus; }
    public void setParticipationStatus(String participationStatus) { this.participationStatus = participationStatus; }
}
