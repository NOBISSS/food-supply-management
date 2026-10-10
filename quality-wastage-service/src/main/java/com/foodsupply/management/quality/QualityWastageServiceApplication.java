package com.foodsupply.management.quality;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.quality","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class QualityWastageServiceApplication { public static void main(String[] args){SpringApplication.run(QualityWastageServiceApplication.class,args);} }
