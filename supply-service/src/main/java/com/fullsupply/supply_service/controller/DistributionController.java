package com.fullsupply.supply_service.controller;

import com.fullsupply.supply_service.distribution.*;
import com.fullsupply.supply_service.distribution.entity.DistributionCentre;
import com.fullsupply.supply_service.distribution.entity.DistributionPlan;
import com.fullsupply.supply_service.distribution.entity.DistributionPlanItem;
import com.fullsupply.supply_service.distribution.repository.DistributionCentreRepository;
import com.fullsupply.supply_service.distribution.repository.DistributionPlanItemRepository;
import com.fullsupply.supply_service.distribution.repository.DistributionPlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/distribution")
public class DistributionController {

    private final DistributionPlanService planService;
    private final DistributionCentreRepository centreRepository;
    private final DistributionPlanRepository planRepository;
    private final DistributionPlanItemRepository planItemRepository;

    public DistributionController(DistributionPlanService planService,
                                  DistributionCentreRepository centreRepository,
                                  DistributionPlanRepository planRepository,
                                  DistributionPlanItemRepository planItemRepository) {
        this.planService = planService;
        this.centreRepository = centreRepository;
        this.planRepository = planRepository;
        this.planItemRepository = planItemRepository;
    }

    @GetMapping("/centres")
    public List<DistributionCentre> getCentres() {
        return centreRepository.findAll();
    }

    @PostMapping("/centres")
    public DistributionCentre createCentre(@RequestBody DistributionCentre centre) {
        return centreRepository.save(centre);
    }

    public record CreatePlanRequest(
            Long programmeId, LocalDate plannedDate, Integer targetBeneficiaryCount,
            String createdBy, List<DistributionPlanService.RequestedItem> items
    ) {}

    @PostMapping("/plans")
    public DistributionPlanService.PlanCreationResult createPlan(@RequestBody CreatePlanRequest request) {
        return planService.createPlan(
                request.programmeId(), request.plannedDate(), request.targetBeneficiaryCount(),
                request.createdBy(), request.items()
        );
    }

    @GetMapping("/plans")
    public List<DistributionPlan> getPlans() {
        return planRepository.findAll();
    }

    @GetMapping("/plans/{planId}/items")
    public List<DistributionPlanItem> getPlanItems(@PathVariable Long planId) {
        return planItemRepository.findByPlan_PlanId(planId);
    }

    public record ConfirmDeliveryRequest(String updatedBy, String remarks) {}

    @PostMapping("/plan-items/{planItemId}/confirm-delivery")
    public ResponseEntity<String> confirmDelivery(@PathVariable Long planItemId, @RequestBody ConfirmDeliveryRequest request) {
        planService.confirmDelivery(planItemId, request.updatedBy(), request.remarks());
        return ResponseEntity.ok("Delivery confirmed, stock deducted");
    }
}