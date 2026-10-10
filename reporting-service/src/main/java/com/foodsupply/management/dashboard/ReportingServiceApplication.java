package com.foodsupply.management.dashboard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication(scanBasePackages={"com.foodsupply.management.dashboard","com.foodsupply.management.common","com.foodsupply.management.auth.security"})
@EntityScan(basePackages={"com.foodsupply.management.beneficiary.entity","com.foodsupply.management.foodinventory.entity","com.foodsupply.management.procurement.entity","com.foodsupply.management.distribution.entity","com.foodsupply.management.quality.entity"})
@EnableJpaRepositories(basePackages={"com.foodsupply.management.beneficiary.repository","com.foodsupply.management.foodinventory.repository","com.foodsupply.management.procurement.repository","com.foodsupply.management.distribution.repository","com.foodsupply.management.quality.repository"})
public class ReportingServiceApplication { public static void main(String[] args){SpringApplication.run(ReportingServiceApplication.class,args);} }
