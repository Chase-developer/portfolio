package com.chase.portfolio.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chase.portfolio.models.Badge;
import com.chase.portfolio.models.Skill;

@Service
public class BadgeSkillService {
	
	public static final List<Badge> Badges = List.of(
	        new Badge("badge/googlecybersecurity.png", "Google Cybersecurity Professional Certificate", 
	        		"Offered by Google, This validates my skills in Cybersecurity, Linux, SQL, and Python, covering threat detection, "
	        		+ "risk assessment, security operations, and incident response.", 
	        		"https://coursera.org/verify/professional-cert/17SJKKY4FZHR"),
	        new Badge("badge/amazon.png", "Amazon Junior Software Developer Professional Certificate", 
	        		"Offered by Amazon, This validates my skills in Java, HTML, JavaScript, CSS, Git, and SQL, which I applied to build this website.",
	        		"https://coursera.org/verify/professional-cert/19TLFUOSFZYO"),
	        new Badge("badge/oracle.png", "Oracle Cloud Infrastructure Foundations", 
	        		"Offered by Oracle, Validates my understanding of Oracle Cloud’s core infrastructure, including compute, storage, and networking services.",
	        		"https://coursera.org/verify/R2PR6ZO47YMU"),
	        new Badge("badge/oracle.png", "Introduction to Oracle Cloud Essentials", 
	        		"Offered by Oracle, Covers key Oracle Cloud concepts, best practices, and fundamental cloud deployment strategies.",
	        		"https://coursera.org/verify/9X3O0K3BTB3J"),
	        new Badge("badge/unicolorado.png", "C++ Programming for Unreal Game Development Specialization", 
	        		"Offered by the University of Colorado System, validates my skills in C++ programming for Unreal Engine, "
	        		+ "focusing on game development principles and real-time application design.", 
	        		"https://coursera.org/verify/specialization/LUUVA3VW2SSQ"),
	        new Badge("badge/uniillinois.png", "Accelerated Computer Science Fundamentals Specialization",
	        		"Offered by the University of Illinois Urbana-Champaign, validates my skills in C++ and core computer science fundamentals, "
	        		+ "including algorithms, data structures, and problem-solving.", 
	        		"https://coursera.org/verify/specialization/74B8GCQ0W9W5"),
	        new Badge("badge/htb.png", "HackTheBox Hacker Rank", 
	        		"Offered By HackTheBox, Red Teaming & Penetration Testing badge, placing in the top 2% of rarity, demonstrating my offensive security skills.",
	        		"https://www.hackthebox.com/achievement/badge/2297566/215"),
	        new Badge("badge/isc2cc.png", "ISC2 Certified In Cybersecurity", 
	        		"[Awaiting Exam, Training Certificate] Offered By ISC2, "
	        		+ "validates my foundational knowledge in "
	        		+ "cybersecurity, covering security principles, network security, access control, risk management, and incident response.",
	        		"/images/isc2cctraining.png")
	        );
	
	public static final List<Skill> FullStackSkills = List.copyOf(Arrays.asList(
	        new Skill("logo/java.png", "Java"),
	        new Skill("logo/javascript.png", "Javascript"),
	        new Skill("logo/css.png", "CSS"),
	        new Skill("logo/html.png", "HTML"),
	        new Skill("logo/cpp.png", "C++"),
	        new Skill("logo/python.png", "Python"),
	        new Skill("logo/git.png", "Git"),
	        new Skill("logo/springboot.png", "Springboot"),
	        new Skill("logo/thymeleaf.png", "Thymeleaf"),
	        new Skill("logo/junit.png", "JUnit"),
	        new Skill("logo/gradle.png", "Gradle"),
	        new Skill("logo/maven.png", "Maven"),
	        new Skill("logo/jdbc.png", "JDBC"),
	        new Skill("logo/mybatis.png", "MyBatis"),
	        new Skill("logo/mysql.png", "MySQL"),
	        new Skill("logo/nosql.png", "NoSQL"),
	        new Skill("logo/sqlite.png", "SQLite")
	        
	        ));
	
	public static final List<Skill> CybersecuritySkills = List.of(
	        new Skill("logo/burpsuite.png", "Burpsuite"),
	        new Skill("logo/splunk.png", "Splunk"),
	        new Skill("logo/chronicle.png", "Chronicle"),
	        new Skill("logo/ghidra.png", "Ghidra"),
	        new Skill("logo/cyberchef.png", "Cyberchef"),
	        new Skill("logo/exploitdb.png", "ExploitDB"),
	        new Skill("logo/metasploit.png", "Metasploit"),
	        new Skill("logo/cutter.png", "Cutter"),
	        new Skill("logo/wireshark.png", "Wireshark"),
	        new Skill("logo/mitreattack.png", "Mitre Att&ck"),
	        new Skill("logo/crackstation.png", "CrackStation")
	        
	        );
	
	public static final List<Skill> DevOpsCloudSkills = List.of(
	        new Skill("logo/githubactions.png", "Github Actions"),
	        new Skill("logo/docker.png", "Docker"),
	        new Skill("logo/kubernetes.png", "Kubernetes"),
	        new Skill("logo/oraclecloud.png", "Oracle Cloud"),
	        new Skill("logo/amazoncloud.png", "Amazon Cloud")
	        
	 );

}
