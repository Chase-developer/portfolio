package com.chase.portfolio.services;

import java.util.List;

import com.chase.portfolio.models.Chapter;

public class JourneyService {
	
	private static Chapter chp(int chp, String title)
	{
		return new Chapter(chp, title);
	}
	
	public static final List<Chapter> Chapters = List.of(
			chp(1, "A Career Pivot"),
			chp(2, "Learning with Limited Resources"),
			chp(3, "Solidifying My Learning with HackTheBox"),
			chp(4, "The Portfolio Website, Tangible Proofs"),
			chp(5, "Integrating Oracle Cloud"),
			chp(6, "Implementing Docker for Deployment"),
			chp(7, "Configuring Environment Properties for Seamless Testing"),
			chp(8, "Registering a Domain and Configuring DNS Records"),
			chp(9, "Attempting Kubernetes Deployment"),
			chp(10, "Enhancing the Website’s Appearance and Responsiveness"),
			chp(11, "Returning to HackTheBox – Blue Team Focus"),
			chp(12, "Malware Analysis & Threat Intelligence")
			
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
