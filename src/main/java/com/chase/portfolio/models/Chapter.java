package com.chase.portfolio.models;

public class Chapter {
	
    private final String title;
    private final String name;
    private final int chp;
    
    public Chapter(int chp, String title, String name)
    {
    	this.name = name;
    	this.title = title;
    	this.chp = chp;
    }
    
    public int getChp()
    {
    	return chp;
    }
    
    public void setChp(int chp) {}
    
    public String getChapterLink()
    {
    	return "journey/" + chp; 
    }
    
    public String getFileLink()
    {
    	return "journey/journey_" + chp;
    }
    
    public String getTitle()
    {
    	return "Chapter " + chp + " : " + title;
    }
    
    public String getName()
    {
    	return "Chapter " + chp + " : " + name;
    }
    
    public void setName(String name) {}
    
    

}
