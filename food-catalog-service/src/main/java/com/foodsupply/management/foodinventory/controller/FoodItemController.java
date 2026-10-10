package com.foodsupply.management.foodinventory.controller;
import com.foodsupply.management.foodinventory.dto.FoodItemRequest;
import com.foodsupply.management.foodinventory.entity.FoodItem;
import com.foodsupply.management.foodinventory.service.FoodItemService;
import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/food-items") public class FoodItemController {
 private final FoodItemService service;public FoodItemController(FoodItemService s){service=s;}
 @GetMapping public List<FoodItem> all(){return service.findAll();}@GetMapping("/{id}") public FoodItem one(@PathVariable Long id){return service.findById(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public FoodItem create(@RequestBody FoodItemRequest r){return service.create(r);}
 @PutMapping("/{id}") public FoodItem update(@PathVariable Long id,@RequestBody FoodItemRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
