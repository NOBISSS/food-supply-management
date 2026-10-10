package com.foodsupply.management.inventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.inventory","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class InventoryServiceApplication { public static void main(String[] args){SpringApplication.run(InventoryServiceApplication.class,args);} }
