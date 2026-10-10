package com.foodsupply.management.beneficiary.dto;
import java.time.LocalDate;
public record BeneficiaryRequest(String name, LocalDate dateOfBirth, String gender, String address, String phone, String eligibilityStatus) {}
