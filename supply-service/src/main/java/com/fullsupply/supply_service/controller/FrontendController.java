package com.fullsupply.supply_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/catalog")
    public String catalog() {return "forward:/catalog/index.html";}

    @GetMapping("/distribution/")
    public String distribution() {
        return "forward:/distribution/index.html";
    }

    @GetMapping("/inventory/")
    public String inventory() {
        return "forward:/inventory/index.html";
    }

    @GetMapping("/procurement/")
    public String procurement() {
        return "forward:/procurement/index.html";
    }
}
