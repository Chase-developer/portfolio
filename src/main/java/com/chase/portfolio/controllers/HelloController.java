package com.chase.portfolio.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.chase.portfolio.services.BadgeSkillService;

@Controller
public class HelloController {

    @GetMapping("/")
    public String main(Model model) {
    	model.addAttribute("badges", BadgeSkillService.Badges);
    	model.addAttribute("fullstack", BadgeSkillService.FullStackSkills);
    	model.addAttribute("cybersecurity", BadgeSkillService.CybersecuritySkills);
    	model.addAttribute("devops", BadgeSkillService.DevOpsCloudSkills);
        return "index";  // Maps to index.html in templates/
    }
    
    @GetMapping("/error")
    public String error(Model model) {
        return "error";  // Maps to index.html in templates/
    }
}
