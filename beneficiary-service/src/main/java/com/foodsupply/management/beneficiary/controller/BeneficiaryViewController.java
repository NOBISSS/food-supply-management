package com.foodsupply.management.beneficiary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BeneficiaryViewController {

    @GetMapping({"/", "/beneficiaries", "/beneficiaries.html"})
    public String beneficiaries() {
        return "beneficiaries";
    }

    @GetMapping({"/nutrition-programmes", "/nutrition-programmes.html"})
    public String nutritionProgrammes() {
        return "nutrition-programmes";
    }
}
