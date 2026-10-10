package com.fullsupply.supply_service.distribution;

import com.fullsupply.supply_service.catalog.entity.FoodItem;
import com.fullsupply.supply_service.distribution.entity.DistributionCentre;
import com.fullsupply.supply_service.distribution.entity.DistributionPlan;
import com.fullsupply.supply_service.distribution.entity.DistributionPlanItem;
import com.fullsupply.supply_service.distribution.entity.DistributionStatusLog;
import com.fullsupply.supply_service.distribution.repository.DistributionPlanItemRepository;
import com.fullsupply.supply_service.distribution.repository.DistributionPlanRepository;
import com.fullsupply.supply_service.distribution.repository.DistributionStatusLogRepository;
import com.fullsupply.supply_service.inventory.BatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class DistributionPlanService {

    private final DistributionPlanRepository planRepository;
    private final DistributionPlanItemRepository planItemRepository;
    private final DistributionStatusLogRepository statusLogRepository;
    private final BatchService batchService;

    public DistributionPlanService(DistributionPlanRepository planRepository,
                                   DistributionPlanItemRepository planItemRepository,
                                   DistributionStatusLogRepository statusLogRepository,
                                   BatchService batchService) {
        this.planRepository = planRepository;
        this.planItemRepository = planItemRepository;
        this.statusLogRepository = statusLogRepository;
        this.batchService = batchService;
    }

    public record RequestedItem(Long beneficiaryId, Long foodItemId, BigDecimal quantity, Long centreId) {}

    public record ShortageInfo(Long foodItemId, BigDecimal totalRequested, BigDecimal totalAvailable, BigDecimal shortfall) {}

    public record PlanCreationResult(DistributionPlan plan, List<ShortageInfo> shortages) {}


    @Transactional
    public PlanCreationResult createPlan(Long programmeId, LocalDate plannedDate,
                                         Integer targetBeneficiaryCount, String createdBy,
                                         List<RequestedItem> items) {

        // Aggregate requested quantity per food item across all beneficiaries in this plan
        Map<Long, BigDecimal> requestedByFoodItem = new HashMap<>();
        for (RequestedItem item : items) {
            requestedByFoodItem.merge(item.foodItemId(), item.quantity(), BigDecimal::add);
        }

        // Check availability and collect shortages
        List<ShortageInfo> shortages = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : requestedByFoodItem.entrySet()) {
            BigDecimal available = batchService.getTotalAvailable(entry.getKey());
            if (available.compareTo(entry.getValue()) < 0) {
                shortages.add(new ShortageInfo(
                        entry.getKey(), entry.getValue(), available,
                        entry.getValue().subtract(available)
                ));
            }
        }

        // Create the plan
        DistributionPlan plan = new DistributionPlan();
        plan.setProgrammeId(programmeId);
        plan.setPlannedDate(plannedDate);
        plan.setTargetBeneficiaryCount(targetBeneficiaryCount);
        plan.setCreatedBy(createdBy);
        plan.setStatus(shortages.isEmpty() ? "DRAFT" : "DRAFT_WITH_SHORTAGE");
        DistributionPlan savedPlan = planRepository.save(plan);

        // Create plan items
        for (RequestedItem item : items) {
            DistributionPlanItem planItem = new DistributionPlanItem();
            planItem.setPlan(savedPlan);
            planItem.setBeneficiaryId(item.beneficiaryId());

            FoodItem foodItem = new FoodItem();
            foodItem.setFoodItemId(item.foodItemId());
            planItem.setFoodItem(foodItem);

            planItem.setPlannedQuantity(item.quantity());

            DistributionCentre centre = new DistributionCentre();
            centre.setCentreId(item.centreId());
            planItem.setCentre(centre);

            planItemRepository.save(planItem);
        }

        return new PlanCreationResult(savedPlan, shortages);
    }


    @Transactional
    public void confirmDelivery(Long planItemId, String updatedBy, String remarks) {
        DistributionPlanItem planItem = planItemRepository.findById(planItemId)
                .orElseThrow(() -> new IllegalArgumentException("Plan item not found: " + planItemId));

        batchService.deductStockFefo(
                planItem.getFoodItem().getFoodItemId(),
                planItem.getPlannedQuantity(),
                planItemId,
                "Distribution delivery"
        );

        DistributionStatusLog log = new DistributionStatusLog();
        log.setPlanItem(planItem);
        log.setStatus("DELIVERED");
        log.setUpdatedDate(LocalDate.now());
        log.setUpdatedBy(updatedBy);
        log.setRemarks(remarks);
        statusLogRepository.save(log);
    }
}