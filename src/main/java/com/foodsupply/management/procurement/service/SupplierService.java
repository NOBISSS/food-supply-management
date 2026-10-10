package com.foodsupply.management.procurement.service;
import com.foodsupply.management.common.exception.ResourceNotFoundException;import com.foodsupply.management.procurement.dto.SupplierRequest;import com.foodsupply.management.procurement.entity.Supplier;import com.foodsupply.management.procurement.repository.SupplierRepository;import org.springframework.stereotype.Service;import java.util.List;
@Service public class SupplierService {private final SupplierRepository repository;public SupplierService(SupplierRepository r){repository=r;}
 public List<Supplier> findAll(){return repository.findAll();}public Supplier findById(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Supplier",id));}
 public Supplier create(SupplierRequest r){return repository.save(map(new Supplier(),r));}public Supplier update(Long id,SupplierRequest r){return repository.save(map(findById(id),r));}public void delete(Long id){repository.delete(findById(id));}
 private Supplier map(Supplier s,SupplierRequest r){s.setName(r.name());s.setContactPerson(r.contactPerson());s.setPhone(r.phone());s.setEmail(r.email());return s;}}
