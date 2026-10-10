package com.foodsupply.management.beneficiary.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String name;
    private LocalDate dateOfBirth;
    @Column(length = 20) private String gender;
    @Column(length = 255) private String address;
    @Column(length = 20) private String phone;
    @Column(nullable = false, length = 20) private String eligibilityStatus = "PENDING";
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate value){dateOfBirth=value;}
    public String getGender(){return gender;} public void setGender(String value){gender=value;}
    public String getAddress(){return address;} public void setAddress(String value){address=value;}
    public String getPhone(){return phone;} public void setPhone(String value){phone=value;}
    public String getEligibilityStatus(){return eligibilityStatus;} public void setEligibilityStatus(String value){eligibilityStatus=value;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
