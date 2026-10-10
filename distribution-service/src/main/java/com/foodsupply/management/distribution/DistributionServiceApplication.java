package com.foodsupply.management.distribution;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.distribution","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class DistributionServiceApplication { public static void main(String[] args){SpringApplication.run(DistributionServiceApplication.class,args);} }
