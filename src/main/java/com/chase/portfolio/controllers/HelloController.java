package com.chase.portfolio.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chase.portfolio.services.BadgeSkillService;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index(Model model) {
    	return "redirect:/home";
    }
    
    
    
    
    
    @GetMapping("/home")
    public String home(Model model) {
    	model.addAttribute("badges", BadgeSkillService.Badges);
    	model.addAttribute("fullstack", BadgeSkillService.FullStackSkills);
    	model.addAttribute("cybersecurity", BadgeSkillService.CybersecuritySkills);
    	model.addAttribute("devops", BadgeSkillService.DevOpsCloudSkills);
        return "home";  // Maps to index.html in templates/
    }
    
//    @GetMapping("/error")
//    public String error(Model model) {
//    	model.addAttribute("pageTitle", "Error");
//        return "error";  // Maps to index.html in templates/
//    }
    
    
    
    @GetMapping("/home/journey")
    public String journey(Model model) {
        return "journey";  // Maps to index.html in templates/
    }
    
    @GetMapping("/home/journey/jan")
    public String journey_jan(Model model) {
        return "journeys/journey_jan";  // Maps to index.html in templates/
    }
    
//    @GetMapping("/.well-known/pki-validation/E86F8C4B2F4DFDBADD3B43031B3C303D.txt")
//    @ResponseBody
//    public String comodo(Model model) {
//        return "AA59DC691ED0E5187250B75BC577BEFA1C20AB84CC57CE9B99187DDAEB9A129D comodoca.com 67e59a8207be3";
//    }
}
