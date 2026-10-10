package com.foodsupply.management.beneficiary.service;

import com.foodsupply.management.beneficiary.dto.BeneficiaryRequest;
import com.foodsupply.management.beneficiary.dto.BeneficiaryResponse;
import com.foodsupply.management.beneficiary.entity.BeneficiaryProfile;
import com.foodsupply.management.beneficiary.repository.BeneficiaryProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryProfileRepository repository;

    public BeneficiaryService(BeneficiaryProfileRepository repository) {
        this.repository = repository;
    }

    public BeneficiaryResponse create(BeneficiaryRequest request) {
        BeneficiaryProfile profile = new BeneficiaryProfile();
        applyRequest(profile, request);
        repository.save(profile);
        return toResponse(profile);
    }

    public List<BeneficiaryResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public BeneficiaryResponse findById(Long id) {
        BeneficiaryProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + id));
        return toResponse(profile);
    }

    public BeneficiaryResponse update(Long id, BeneficiaryRequest request) {
        BeneficiaryProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + id));
        applyRequest(profile, request);
        repository.save(profile);
        return toResponse(profile);
    }

    public void deactivate(Long id) {
        BeneficiaryProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + id));
        profile.setStatus("INACTIVE");
        repository.save(profile);
    }
    
    public void reactivate(Long id) {
        BeneficiaryProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + id));
        profile.setStatus("ACTIVE");
        repository.save(profile);
    }

    private void applyRequest(BeneficiaryProfile profile, BeneficiaryRequest request) {
        profile.setFullName(request.getFullName());
        profile.setGender(request.getGender());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setHouseholdId(request.getHouseholdId());
    }

    private BeneficiaryResponse toResponse(BeneficiaryProfile p) {
        return new BeneficiaryResponse(p.getId(), p.getFullName(), p.getGender(), p.getDateOfBirth(),
                p.getPhone(), p.getAddress(), p.getHouseholdId(), p.getRegisteredDate(), p.getStatus());
    }
}
