package com.chase.portfolio.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chase.portfolio.services.HTBReport;
import com.chase.portfolio.services.HTBService;

@Controller
public class MarkdownController {
	
    
    @GetMapping("/htb/{reportName}")
    public String htb(@PathVariable("reportName") String reportName, Model model) {
    	HTBReport reportTitle = HTBService.getReport(reportName);
    	if (reportTitle == null)
    		return "error";
        model.addAttribute("reportTitle", reportTitle.getTitle()); // Dynamic title
        model.addAttribute("HTBImg", reportTitle.getImage());
        model.addAttribute("HTBVerify", reportTitle.getVerifyLink());
        model.addAttribute("reportTitle", reportTitle.getTitle());
        model.addAttribute("reportFile", "/texts/" + reportName + ".txt"); // Dynamic file URL
        model.addAttribute("reports", HTBService.Reports);
        return "markdown_htb";
    }
}
