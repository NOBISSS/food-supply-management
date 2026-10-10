package com.foodsupply.qualityreport.quality.repository;

import com.foodsupply.qualityreport.quality.entity.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QualityInspectionRepository extends JpaRepository<QualityInspection, Long> {
    List<QualityInspection> findByBatchId(Long batchId);
}
