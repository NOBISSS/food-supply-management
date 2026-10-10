package com.foodsupply.management.beneficiary.dto;

import java.time.LocalDate;

public class ParticipationResponse {
    private Long id;
    private Long programmeId;
    private Long beneficiaryId;
    private String beneficiaryName;
    private LocalDate enrolmentDate;
    private String participationStatus;

    public ParticipationResponse(Long id, Long programmeId, Long beneficiaryId, String beneficiaryName,
                                  LocalDate enrolmentDate, String participationStatus) {
        this.id = id;
        this.programmeId = programmeId;
        this.beneficiaryId = beneficiaryId;
        this.beneficiaryName = beneficiaryName;
        this.enrolmentDate = enrolmentDate;
        this.participationStatus = participationStatus;
    }

    public Long getId() { return id; }
    public Long getProgrammeId() { return programmeId; }
    public Long getBeneficiaryId() { return beneficiaryId; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public LocalDate getEnrolmentDate() { return enrolmentDate; }
    public String getParticipationStatus() { return participationStatus; }
}
