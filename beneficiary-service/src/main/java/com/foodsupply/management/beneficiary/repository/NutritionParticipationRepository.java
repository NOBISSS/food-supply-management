package com.foodsupply.management.beneficiary.repository;

import com.foodsupply.management.beneficiary.entity.NutritionParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutritionParticipationRepository extends JpaRepository<NutritionParticipation, Long> {

    List<NutritionParticipation> findByProgrammeId(Long programmeId);

    List<NutritionParticipation> findByBeneficiaryId(Long beneficiaryId);

    boolean existsByProgrammeIdAndBeneficiaryIdAndParticipationStatus(
            Long programmeId, Long beneficiaryId, String participationStatus);
}
