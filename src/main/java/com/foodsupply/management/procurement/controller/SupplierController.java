package com.foodsupply.management.procurement.controller;
import com.foodsupply.management.procurement.dto.SupplierRequest;import com.foodsupply.management.procurement.entity.Supplier;import com.foodsupply.management.procurement.service.SupplierService;import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/suppliers") public class SupplierController {private final SupplierService service;public SupplierController(SupplierService s){service=s;}
 @GetMapping public List<Supplier> all(){return service.findAll();}@GetMapping("/{id}") public Supplier one(@PathVariable Long id){return service.findById(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Supplier create(@RequestBody SupplierRequest r){return service.create(r);}@PutMapping("/{id}") public Supplier update(@PathVariable Long id,@RequestBody SupplierRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}}
