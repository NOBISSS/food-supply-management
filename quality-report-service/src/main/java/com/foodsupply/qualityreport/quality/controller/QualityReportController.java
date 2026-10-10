package com.foodsupply.qualityreport.quality.controller;

import com.foodsupply.qualityreport.quality.entity.QualityInspection;
import com.foodsupply.qualityreport.quality.entity.QualityWastageRecord;
import com.foodsupply.qualityreport.quality.entity.QualityCorrectiveAction;
import com.foodsupply.qualityreport.quality.repository.QualityInspectionRepository;
import com.foodsupply.qualityreport.quality.repository.QualityWastageRecordRepository;
import com.foodsupply.qualityreport.quality.repository.QualityCorrectiveActionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quality")
public class QualityReportController {

    private final QualityInspectionRepository inspectionRepo;
    private final QualityWastageRecordRepository wastageRepo;
    private final QualityCorrectiveActionRepository actionRepo;

    public QualityReportController(QualityInspectionRepository inspectionRepo,
                                    QualityWastageRecordRepository wastageRepo,
                                    QualityCorrectiveActionRepository actionRepo) {
        this.inspectionRepo = inspectionRepo;
        this.wastageRepo = wastageRepo;
        this.actionRepo = actionRepo;
    }

    // ---------- Inspections ----------
    @GetMapping("/inspections")
    public List<QualityInspection> getAllInspections() {
        return inspectionRepo.findAll();
    }

    @GetMapping("/inspections/batch/{batchId}")
    public List<QualityInspection> getInspectionsByBatch(@PathVariable Long batchId) {
        return inspectionRepo.findByBatchId(batchId);
    }

    @PostMapping("/inspections")
    public ResponseEntity<QualityInspection> createInspection(@RequestBody QualityInspection inspection) {
        return ResponseEntity.ok(inspectionRepo.save(inspection));
    }

    // ---------- Wastage ----------
    @GetMapping("/wastage")
    public List<QualityWastageRecord> getAllWastage() {
        return wastageRepo.findAll();
    }

    @GetMapping("/wastage/batch/{batchId}")
    public List<QualityWastageRecord> getWastageByBatch(@PathVariable Long batchId) {
        return wastageRepo.findByBatchId(batchId);
    }

    @PostMapping("/wastage")
    public ResponseEntity<QualityWastageRecord> createWastage(@RequestBody QualityWastageRecord record) {
        return ResponseEntity.ok(wastageRepo.save(record));
    }

    // ---------- Corrective Actions ----------
    @GetMapping("/corrective-actions")
    public List<QualityCorrectiveAction> getAllActions() {
        return actionRepo.findAll();
    }

    @GetMapping("/corrective-actions/wastage/{wastageId}")
    public List<QualityCorrectiveAction> getActionsByWastage(@PathVariable Long wastageId) {
        return actionRepo.findByWastageId(wastageId);
    }

    @PostMapping("/corrective-actions")
    public ResponseEntity<QualityCorrectiveAction> createAction(@RequestBody QualityCorrectiveAction action) {
        return ResponseEntity.ok(actionRepo.save(action));
    }
}
