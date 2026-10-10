package com.foodsupply.management.beneficiary.dto;

import java.time.LocalDate;

public class BeneficiaryResponse {
    private Long id;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;
    private String householdId;
    private LocalDate registeredDate;
    private String status;

    public BeneficiaryResponse(Long id, String fullName, String gender, LocalDate dateOfBirth,
                                String phone, String address, String householdId,
                                LocalDate registeredDate, String status) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.address = address;
        this.householdId = householdId;
        this.registeredDate = registeredDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getHouseholdId() { return householdId; }
    public LocalDate getRegisteredDate() { return registeredDate; }
    public String getStatus() { return status; }
}
