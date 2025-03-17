package com.chase.portfolio.services;

import java.util.List;

public class BadgeService {
	
	public static final List<Badge> Badges = List.of(
	        new Badge("googlecybersecurity.png", "Google Cybersecurity Professional Certificate", 
	        		"Offered by Google, This validates my skills in Cybersecurity, Linux, SQL, and Python, covering threat detection, "
	        		+ "risk assessment, security operations, and incident response.", 
	        		"https://coursera.org/verify/professional-cert/17SJKKY4FZHR"),
	        new Badge("amazon.png", "Amazon Junior Software Developer Professional Certificate", 
	        		"Offered by Amazon, This validates my skills in Java, HTML, JavaScript, CSS, Git, and SQL, which I applied to build this website.",
	        		"https://coursera.org/verify/professional-cert/19TLFUOSFZYO"),
	        new Badge("oracle.png", "Oracle Cloud Infrastructure Foundations", 
	        		"Offered by Oracle, Validates my understanding of Oracle Cloud’s core infrastructure, including compute, storage, and networking services.",
	        		"https://coursera.org/verify/R2PR6ZO47YMU"),
	        new Badge("oracle.png", "Introduction to Oracle Cloud Essentials", 
	        		"Offered by Oracle, Covers key Oracle Cloud concepts, best practices, and fundamental cloud deployment strategies.",
	        		"https://coursera.org/verify/9X3O0K3BTB3J"),
	        new Badge("unicolorado.png", "C++ Programming for Unreal Game Development Specialization", 
	        		"Offered by the University of Colorado System, validates my skills in C++ programming for Unreal Engine, "
	        		+ "focusing on game development principles and real-time application design.", 
	        		"https://coursera.org/verify/specialization/LUUVA3VW2SSQ"),
	        new Badge("uniillinois.png", "Accelerated Computer Science Fundamentals Specialization",
	        		"Offered by the University of Illinois Urbana-Champaign, validates my skills in C++ and core computer science fundamentals, "
	        		+ "including algorithms, data structures, and problem-solving.", 
	        		"https://coursera.org/verify/specialization/74B8GCQ0W9W5"),
	        new Badge("htb.png", "HackTheBox Hacker Rank", 
	        		"Offered By HackTheBox, Red Teaming & Penetration Testing badge, placing in the top 2% of rarity, demonstrating my offensive security skills.",
	        		"https://www.hackthebox.com/achievement/badge/2297566/215"),
	        new Badge("isc2cc.png", "ISC2 Certified In Cybersecurity", 
	        		"[Awaiting Exam, Training Certificate] Offered By ISC2, "
	        		+ "validates my foundational knowledge in "
	        		+ "cybersecurity, covering security principles, network security, access control, risk management, and incident response.",
	        		"/images/isc2cctraining.png")
	        );

}
