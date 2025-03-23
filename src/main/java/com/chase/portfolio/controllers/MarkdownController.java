package com.chase.portfolio.controllers;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.vavr.collection.HashMap;

@Controller
public class MarkdownController {
	
	private static Entry<String, String> entry(String name, String title)
	{
		return new AbstractMap.SimpleEntry<String, String>(name, title);
	}
	//private static final HashSet<String> Reports = HashSet.of("brutus", "lockpick", "escapetwo");
	private static final HashMap<String, String> Reports = HashMap.ofEntries(
			entry("brutus", "HackTheBox - Sherlock Brutus (DFIR)"),
			entry("lockpick", "HackTheBox - Sherlock Lockpick (Malware Analysis)"),
			entry("escapetwo", "HackTheBox - Machine EscapeTwo (Red Team Penetration Testing)")
			);
//    @GetMapping("/htb")
//    public String htb(Model model) {
//        return "htb/markdown_report";
//    }
    
//    @GetMapping("/htb/{reportName}")
//    public String htbReport(@PathVariable("reportName") String reportName, Model model) {
//        // Whitelist allowed report names to prevent directory traversal
//
//        if (!Reports.contains(reportName)) {
//            return "error/404"; // Return a safe error page
//        }
//
//        return "htb/" + reportName; // Maps to templates/htb/{reportName}.html
//    }
    
    @GetMapping("/htb/{reportName}")
    public String htb(@PathVariable("reportName") String reportName, Model model) {
    	if (!Reports.containsKey(reportName)) {
            return "error"; // Return a safe error page
        }
    	String reportTitle = Reports.get(reportName).get();
        model.addAttribute("reportTitle", reportTitle); // Dynamic title
        model.addAttribute("reportFile", "/texts/" + reportName + ".txt"); // Dynamic file URL
        return "markdown";
    }
}
