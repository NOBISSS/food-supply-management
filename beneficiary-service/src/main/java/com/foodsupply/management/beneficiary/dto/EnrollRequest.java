package com.foodsupply.management.beneficiary.dto;

import jakarta.validation.constraints.NotNull;

public class EnrollRequest {

    @NotNull(message = "Beneficiary ID is required")
    private Long beneficiaryId;

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }
}
