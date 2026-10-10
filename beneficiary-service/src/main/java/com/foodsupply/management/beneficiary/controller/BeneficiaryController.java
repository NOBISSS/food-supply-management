package com.foodsupply.management.beneficiary.controller;

import com.foodsupply.management.beneficiary.dto.BeneficiaryRequest;
import com.foodsupply.management.beneficiary.dto.BeneficiaryResponse;
import com.foodsupply.management.beneficiary.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService service;

    public BeneficiaryController(BeneficiaryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> create(@Valid @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> update(@PathVariable Long id, @Valid @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
