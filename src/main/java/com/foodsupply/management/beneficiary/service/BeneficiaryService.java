package com.foodsupply.management.beneficiary.service;
import com.foodsupply.management.beneficiary.dto.BeneficiaryRequest;
import com.foodsupply.management.beneficiary.entity.Beneficiary;
import com.foodsupply.management.beneficiary.repository.BeneficiaryRepository;
import com.foodsupply.management.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class BeneficiaryService {
    private final BeneficiaryRepository repository;
    public BeneficiaryService(BeneficiaryRepository repository){this.repository=repository;}
    public List<Beneficiary> findAll(){return repository.findAll();}
    public Beneficiary findById(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Beneficiary",id));}
    public Beneficiary create(BeneficiaryRequest request){return repository.save(map(new Beneficiary(),request));}
    public Beneficiary update(Long id,BeneficiaryRequest request){return repository.save(map(findById(id),request));}
    public void delete(Long id){repository.delete(findById(id));}
    private Beneficiary map(Beneficiary b,BeneficiaryRequest r){b.setName(r.name());b.setDateOfBirth(r.dateOfBirth());b.setGender(r.gender());b.setAddress(r.address());b.setPhone(r.phone());if(r.eligibilityStatus()!=null)b.setEligibilityStatus(r.eligibilityStatus());return b;}
}
