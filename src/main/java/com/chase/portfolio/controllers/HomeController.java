package com.chase.portfolio.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chase.portfolio.models.Chapter;
import com.chase.portfolio.models.HTBReport;
import com.chase.portfolio.services.BadgeSkillService;
import com.chase.portfolio.services.HTBService;
import com.chase.portfolio.services.JourneyService;

@Controller
public class HomeController {
	
	@GetMapping("/htb")
    public String htb(Model model) {
		model.addAttribute("HTBImg", "htb/hackerrank.png");
        model.addAttribute("HTBVerify", "https://www.hackthebox.com/achievement/badge/2297566/215");
    	model.addAttribute("title", "HackTheBox Intro");
        model.addAttribute("file", "/texts/htb/hackthebox_intro.txt"); // Dynamic file URL
        model.addAttribute("reports", HTBService.Reports);
        return "markdowns/hackthebox";  // Maps to index.html in templates/
    }
	
	@GetMapping("/htb/{reportName}")
    public String htb_reports(@PathVariable("reportName") String reportName, Model model) {
    	HTBReport reportTitle = HTBService.getReport(reportName);
    	if (reportTitle == null)
    		return "error";
        model.addAttribute("HTBImg", reportTitle.getImage());
        model.addAttribute("HTBVerify", reportTitle.getVerifyLink());
        model.addAttribute("title", reportTitle.getTitle());
        model.addAttribute("file", "/texts/htb/" + reportName + ".txt"); // Dynamic file URL
        model.addAttribute("reports", HTBService.Reports);
        return "markdowns/hackthebox";
    }

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
    
    
    
    @GetMapping("/journey")
    public String journey(Model model) {
    	model.addAttribute("title", "Journey Timeline");
        model.addAttribute("file", "/texts/journey/journey_timeline.txt"); // Dynamic file URL
        model.addAttribute("chapters", JourneyService.Chapters);
        
        model.addAttribute("prev", "1");
        model.addAttribute("next", "1");
        model.addAttribute("showPrev", false);  // or false based on your logic
        model.addAttribute("showNext", false);  // or false based on your logic
        
        return "markdowns/chapters";  // Maps to index.html in templates/
    }
    
    @GetMapping("/journey/{chapter}")
    public String journey_chapter(@PathVariable("chapter") String chapter, Model model) {
    	if (!JourneyService.isValidChp(chapter))
    		return "error";
    	int chp_i = Integer.valueOf(chapter);
    	Chapter chp = JourneyService.getChapter(chp_i);
    	if (chp == null)
    		return "error";
    	model.addAttribute("prev", String.valueOf(chp_i - 1));
        model.addAttribute("next", String.valueOf(chp_i + 1));
        model.addAttribute("showPrev", JourneyService.getChapter(chp_i - 1) != null);  // or false based on your logic
        model.addAttribute("showNext", JourneyService.getChapter(chp_i + 1) != null);  // or false based on your logic
    	
        model.addAttribute("title", chp.getTitle());
        model.addAttribute("file", "/texts/" + chp.getFileLink() + ".txt"); // Dynamic file URL
        model.addAttribute("chapters", JourneyService.Chapters);
        return "markdowns/chapters";
    }
    
//    @GetMapping("/.well-known/pki-validation/E86F8C4B2F4DFDBADD3B43031B3C303D.txt")
//    @ResponseBody
//    public String comodo(Model model) {
//        return "AA59DC691ED0E5187250B75BC577BEFA1C20AB84CC57CE9B99187DDAEB9A129D comodoca.com 67e59a8207be3";
//    }
}
