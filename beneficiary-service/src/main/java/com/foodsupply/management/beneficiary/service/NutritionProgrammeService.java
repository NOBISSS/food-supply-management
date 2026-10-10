package com.foodsupply.management.beneficiary.service;

import com.foodsupply.management.beneficiary.dto.EnrollRequest;
import com.foodsupply.management.beneficiary.dto.NutritionProgrammeRequest;
import com.foodsupply.management.beneficiary.dto.NutritionProgrammeResponse;
import com.foodsupply.management.beneficiary.dto.ParticipationResponse;
import com.foodsupply.management.beneficiary.entity.BeneficiaryProfile;
import com.foodsupply.management.beneficiary.entity.NutritionParticipation;
import com.foodsupply.management.beneficiary.entity.NutritionProgramme;
import com.foodsupply.management.beneficiary.repository.BeneficiaryProfileRepository;
import com.foodsupply.management.beneficiary.repository.NutritionParticipationRepository;
import com.foodsupply.management.beneficiary.repository.NutritionProgrammeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NutritionProgrammeService {

    private final NutritionProgrammeRepository programmeRepository;
    private final NutritionParticipationRepository participationRepository;
    private final BeneficiaryProfileRepository beneficiaryRepository;

    public NutritionProgrammeService(NutritionProgrammeRepository programmeRepository,
                                      NutritionParticipationRepository participationRepository,
                                      BeneficiaryProfileRepository beneficiaryRepository) {
        this.programmeRepository = programmeRepository;
        this.participationRepository = participationRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public NutritionProgrammeResponse create(NutritionProgrammeRequest request) {
        NutritionProgramme programme = new NutritionProgramme();
        applyRequest(programme, request);
        programmeRepository.save(programme);
        return toResponse(programme);
    }

    public List<NutritionProgrammeResponse> findAll() {
        return programmeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public NutritionProgrammeResponse findById(Long id) {
        return toResponse(getProgrammeOrThrow(id));
    }

    public NutritionProgrammeResponse update(Long id, NutritionProgrammeRequest request) {
        NutritionProgramme programme = getProgrammeOrThrow(id);
        applyRequest(programme, request);
        programmeRepository.save(programme);
        return toResponse(programme);
    }

    public void deactivate(Long id) {
        NutritionProgramme programme = getProgrammeOrThrow(id);
        programme.setStatus("CANCELLED");
        programmeRepository.save(programme);
    }

    public void reactivate(Long id) {
        NutritionProgramme programme = getProgrammeOrThrow(id);
        programme.setStatus("ACTIVE");
        programmeRepository.save(programme);
    }

    public ParticipationResponse enroll(Long programmeId, EnrollRequest request) {
        NutritionProgramme programme = getProgrammeOrThrow(programmeId);

        BeneficiaryProfile beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + request.getBeneficiaryId()));

        // One participation row per beneficiary per programme, ever. If a record already
        // exists (COMPLETED or DROPPED), re-activate it instead of inserting a duplicate row.
        NutritionParticipation participation = participationRepository
                .findByProgrammeIdAndBeneficiaryId(programme.getId(), beneficiary.getId())
                .orElse(null);

        if (participation != null) {
            if ("ACTIVE".equals(participation.getParticipationStatus())) {
                throw new RuntimeException("Beneficiary is already actively enrolled in this programme");
            }
            participation.setParticipationStatus("ACTIVE");
            participation.setEnrolmentDate(LocalDate.now());
        } else {
            participation = new NutritionParticipation();
            participation.setProgrammeId(programme.getId());
            participation.setBeneficiaryId(beneficiary.getId());
        }

        participationRepository.save(participation);
        return toParticipationResponse(participation, beneficiary.getFullName());
    }

    public List<ParticipationResponse> listParticipants(Long programmeId) {
        getProgrammeOrThrow(programmeId);
        return participationRepository.findByProgrammeId(programmeId).stream()
                .map(p -> {
                    String name = beneficiaryRepository.findById(p.getBeneficiaryId())
                            .map(BeneficiaryProfile::getFullName)
                            .orElse("Unknown");
                    return toParticipationResponse(p, name);
                })
                .toList();
    }

    public void dropParticipation(Long participationId) {
        NutritionParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found: " + participationId));
        participation.setParticipationStatus("DROPPED");
        participationRepository.save(participation);
    }

    public void completeParticipation(Long participationId) {
        NutritionParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found: " + participationId));
        participation.setParticipationStatus("COMPLETED");
        participationRepository.save(participation);
    }

    private NutritionProgramme getProgrammeOrThrow(Long id) {
        return programmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nutrition programme not found: " + id));
    }

    private void applyRequest(NutritionProgramme programme, NutritionProgrammeRequest request) {
        programme.setProgrammeName(request.getProgrammeName());
        programme.setDescription(request.getDescription());
        programme.setStartDate(request.getStartDate());
        programme.setEndDate(request.getEndDate());
        programme.setTargetGroup(request.getTargetGroup());
    }

    private NutritionProgrammeResponse toResponse(NutritionProgramme p) {
        return new NutritionProgrammeResponse(p.getId(), p.getProgrammeName(), p.getDescription(),
                p.getStartDate(), p.getEndDate(), p.getTargetGroup(), p.getStatus());
    }

    private ParticipationResponse toParticipationResponse(NutritionParticipation p, String beneficiaryName) {
        return new ParticipationResponse(p.getId(), p.getProgrammeId(), p.getBeneficiaryId(), beneficiaryName,
                p.getEnrolmentDate(), p.getParticipationStatus());
    }
}