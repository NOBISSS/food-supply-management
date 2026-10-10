package com.foodsupply.management.foodinventory.service;
import com.foodsupply.management.common.exception.ResourceNotFoundException;
import com.foodsupply.management.foodinventory.dto.FoodItemRequest;
import com.foodsupply.management.foodinventory.entity.FoodItem;
import com.foodsupply.management.foodinventory.repository.FoodItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service public class FoodItemService {
 private final FoodItemRepository repository; public FoodItemService(FoodItemRepository r){repository=r;}
 public List<FoodItem> findAll(){return repository.findAll();} public FoodItem findById(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Food item",id));}
 public FoodItem create(FoodItemRequest r){return repository.save(map(new FoodItem(),r));} public FoodItem update(Long id,FoodItemRequest r){return repository.save(map(findById(id),r));} public void delete(Long id){repository.delete(findById(id));}
 private FoodItem map(FoodItem f,FoodItemRequest r){f.setName(r.name());f.setCategory(r.category());f.setUnit(r.unit());f.setCaloriesPerUnit(r.caloriesPerUnit());f.setProteinPerUnit(r.proteinPerUnit());return f;}
}
