package com.foodsupply.management.foodinventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.foodinventory","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class FoodCatalogServiceApplication { public static void main(String[] args){SpringApplication.run(FoodCatalogServiceApplication.class,args);} }
