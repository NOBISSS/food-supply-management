package com.foodsupply.qualityreport.quality.repository;

import com.foodsupply.qualityreport.quality.entity.QualityWastageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QualityWastageRecordRepository extends JpaRepository<QualityWastageRecord, Long> {
    List<QualityWastageRecord> findByBatchId(Long batchId);
}
