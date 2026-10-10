package com.foodsupply.management.beneficiary.controller;

import com.foodsupply.management.beneficiary.dto.EnrollRequest;
import com.foodsupply.management.beneficiary.dto.NutritionProgrammeRequest;
import com.foodsupply.management.beneficiary.dto.NutritionProgrammeResponse;
import com.foodsupply.management.beneficiary.dto.ParticipationResponse;
import com.foodsupply.management.beneficiary.service.NutritionProgrammeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition-programmes")
public class NutritionProgrammeController {

    private final NutritionProgrammeService service;

    public NutritionProgrammeController(NutritionProgrammeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NutritionProgrammeResponse> create(@Valid @RequestBody NutritionProgrammeRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NutritionProgrammeResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NutritionProgrammeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NutritionProgrammeResponse> update(@PathVariable Long id,
                                                               @Valid @RequestBody NutritionProgrammeRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable Long id) {
        service.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<ParticipationResponse> enroll(@PathVariable Long id,
                                                          @Valid @RequestBody EnrollRequest request) {
        return ResponseEntity.ok(service.enroll(id, request));
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipationResponse>> listParticipants(@PathVariable Long id) {
        return ResponseEntity.ok(service.listParticipants(id));
    }

    @PatchMapping("/participations/{participationId}/drop")
    public ResponseEntity<Void> dropParticipation(@PathVariable Long participationId) {
        service.dropParticipation(participationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/participations/{participationId}/complete")
    public ResponseEntity<Void> completeParticipation(@PathVariable Long participationId) {
        service.completeParticipation(participationId);
        return ResponseEntity.noContent().build();
    }
}
