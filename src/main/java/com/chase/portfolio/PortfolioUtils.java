package com.chase.portfolio;

import java.io.File;

public class PortfolioUtils {
	
	public static boolean isTestProfile() {
		String profile = System.getProperty("spring.profiles.active");
        return profile == null ? false : profile.contains("test");
    }
	
	public static boolean isInProject()
	{
		return new File(".project").exists();
	}

}
