package com.fullsupply.supply_service.catalog;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class FoodCatalogController {

    private final FoodCategoryRepository categoryRepository;
    private final FoodItemRepository itemRepository;

    public FoodCatalogController(FoodCategoryRepository categoryRepository, FoodItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/categories")
    public List<FoodCategory> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/items")
    public List<FoodItem> getItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/items/by-category/{categoryId}")
    public List<FoodItem> getItemsByCategory(@PathVariable Long categoryId) {
        return itemRepository.findByCategory_CategoryId(categoryId);
    }
}