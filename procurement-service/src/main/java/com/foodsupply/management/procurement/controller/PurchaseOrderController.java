package com.foodsupply.management.procurement.controller;
import com.foodsupply.management.procurement.dto.PurchaseOrderRequest;import com.foodsupply.management.procurement.entity.PurchaseOrder;import com.foodsupply.management.procurement.service.PurchaseOrderService;import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/purchase-orders") public class PurchaseOrderController {private final PurchaseOrderService service;public PurchaseOrderController(PurchaseOrderService s){service=s;}
 @GetMapping public List<PurchaseOrder> all(){return service.findAll();}@GetMapping("/{id}") public PurchaseOrder one(@PathVariable Long id){return service.findById(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public PurchaseOrder create(@RequestBody PurchaseOrderRequest r){return service.create(r);}@PutMapping("/{id}") public PurchaseOrder update(@PathVariable Long id,@RequestBody PurchaseOrderRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}}
