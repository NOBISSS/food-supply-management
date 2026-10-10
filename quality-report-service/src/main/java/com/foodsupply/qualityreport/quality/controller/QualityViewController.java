package com.foodsupply.qualityreport.quality.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QualityViewController {

    @GetMapping({"/", "/quality", "/quality.html"})
    public String quality() {
        return "quality";
    }
}
