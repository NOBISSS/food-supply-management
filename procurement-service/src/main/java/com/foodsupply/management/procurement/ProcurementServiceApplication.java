package com.foodsupply.management.procurement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.procurement","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class ProcurementServiceApplication { public static void main(String[] args){SpringApplication.run(ProcurementServiceApplication.class,args);} }
