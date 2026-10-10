package com.foodsupply.management.beneficiary.repository;

import com.foodsupply.management.beneficiary.entity.BeneficiaryProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryProfileRepository extends JpaRepository<BeneficiaryProfile, Long> {
}
