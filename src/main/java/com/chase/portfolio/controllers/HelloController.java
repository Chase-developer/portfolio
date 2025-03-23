package com.chase.portfolio.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.chase.portfolio.services.BadgeService;

@Controller
public class HelloController {

    @GetMapping("/")
    public String main(Model model) {
    	model.addAttribute("badges", BadgeService.Badges);
        return "index";  // Maps to index.html in templates/
    }
    
    @GetMapping("/error")
    public String error(Model model) {
        return "error";  // Maps to index.html in templates/
    }
}
