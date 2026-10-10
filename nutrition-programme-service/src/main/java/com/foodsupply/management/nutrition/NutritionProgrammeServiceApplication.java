package com.foodsupply.management.nutrition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.nutrition","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class NutritionProgrammeServiceApplication { public static void main(String[] args){SpringApplication.run(NutritionProgrammeServiceApplication.class,args);} }
