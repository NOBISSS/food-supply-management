package com.foodsupply.qualityreport.quality.repository;

import com.foodsupply.qualityreport.quality.entity.QualityCorrectiveAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QualityCorrectiveActionRepository extends JpaRepository<QualityCorrectiveAction, Long> {
    List<QualityCorrectiveAction> findByWastageId(Long wastageId);
}
