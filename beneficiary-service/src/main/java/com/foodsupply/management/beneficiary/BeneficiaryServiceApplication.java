package com.foodsupply.management.beneficiary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.beneficiary","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
public class BeneficiaryServiceApplication { public static void main(String[] args){SpringApplication.run(BeneficiaryServiceApplication.class,args);} }
