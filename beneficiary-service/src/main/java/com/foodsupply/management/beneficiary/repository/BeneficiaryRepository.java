package com.foodsupply.management.beneficiary.repository;
import com.foodsupply.management.beneficiary.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {}
