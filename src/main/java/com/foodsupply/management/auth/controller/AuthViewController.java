package com.foodsupply.management.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/register")
    public String registerPage() {
        return "AuthTemplates/register"; // resolves to templates/AuthTemplate/register.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "AuthTemplates/login"; // resolves to templates/AuthTemplate/login.html
    }
}