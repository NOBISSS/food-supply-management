package com.fullsupply.supply_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({"/", "/dashboard", "/dashboard.html"})
    public String home() {
        return "dashboard";
    }

    @GetMapping({"/catalog", "/catalog/", "/catalog/index.html"})
    public String catalog() {return "catalog/index";}

    @GetMapping({"/distribution", "/distribution/", "/distribution/index.html"})
    public String distribution() {
        return "distribution/index";
    }

    @GetMapping({"/inventory", "/inventory/", "/inventory/index.html"})
    public String inventory() {
        return "inventory/index";
    }

    @GetMapping({"/procurement", "/procurement/", "/procurement/index.html"})
    public String procurement() {
        return "procurement/index";
    }
}
