package com.chase.portfolio.services;

import org.springframework.util.StringUtils;

public class HTBReport {
	
	public static enum ReportType
	{
		DFIR("DFIR"), MA("Malware Analysis"), PT("Penetration Testing");
		
		private final String name;
		
		private ReportType(String name)
		{
			this.name = name;
		}
	}
	
	public static enum Team
	{
		Red("Machine"), Blue("Sherlock");
		
		private final String name;
		
		private Team(String name)
		{
			this.name = name;
		}
	}
	
	public static HTBReport red(String id, String verifyLink)
	{
		return new HTBReport(id, Team.Red, ReportType.PT, verifyLink);
	}
	
	public static HTBReport blue(String id, ReportType type, String verifyLink)
	{
		return new HTBReport(id, Team.Blue, type, verifyLink);
	}
	
	/*
	 * entry("brutus", "HackTheBox - Sherlock Brutus (DFIR)", "htb/brutus.png", 
					"https://labs.hackthebox.com/achievement/sherlock/2297566/631"),
	 */
	
	private String id;
    private String verifyLink;
    private Team team;
    private ReportType type;

    private HTBReport(String id, Team team, ReportType type, String verifyLink) {
    	this.id = id;
        this.verifyLink = verifyLink;
        this.team = team;
        this.type = type;
    }
    
    public String getId()
    {
    	return this.id;
    }
    
    public void setId(String id)
    {
    	this.id = id;
    }

    public String getVerifyLink() {
        return verifyLink;
    }
    
    public String getImage() {
        return getLink() + ".png";
    }
    
    public String getTitle() {
        return "HackTheBox - " + team.name + " " + StringUtils.capitalize(id) + " ("+ type.name +")";
    }
    
    public String getName() {
        return team.name + " - " + StringUtils.capitalize(id);
    }
    
    public String getLink() {
        return "htb/" + id;
    }

}
