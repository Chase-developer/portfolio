package com.chase.portfolio.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chase.portfolio.models.Chapter;

@Service
public class JourneyService {
	
	private static Chapter chp(int chp, String title, String name)
	{
		return new Chapter(chp, title, name);
	}
	
	public static final List<Chapter> Chapters = List.of(
			chp(1, "A Career Pivot", "Career Pivot"),
			chp(2, "Learning with Limited Resources", "Limited Resources"),
			chp(3, "Solidifying My Learning with HackTheBox", "HackTheBox"),
			chp(4, "The Portfolio Website, Tangible Proofs", "Portfolio Website"),
			chp(5, "Integrating Oracle Cloud", "Oracle Cloud"),
			chp(6, "Implementing Docker for Deployment", "Docker Deployment"),
			chp(7, "Configuring Environment Properties for Seamless Testing", "Env Properties"),
			chp(8, "Registering a Domain and Configuring DNS Records", "Domain Configure"),
			chp(9, "Attempting Kubernetes Deployment", "Kubernetes Attempt"),
			chp(10, "Enhancing the Website’s Appearance and Responsiveness", "Enhance Website"),
			chp(11, "Returning to HackTheBox – Blue Team Focus", "Blue Team"),
			chp(12, "Malware Analysis & Threat Intelligence", "More Blue Team")
			
			);
	
	public static Chapter getChapter(int chp)
	{
		return (chp > 0 && chp <= Chapters.size()) ? Chapters.get(chp - 1) : null;
	}
	
	public static boolean isValidChp(String chapter)
	{
		return chapter.matches("^\\d+$");
	}
	

}
