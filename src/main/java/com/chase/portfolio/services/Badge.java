package com.chase.portfolio.services;
public class Badge {
    private String image;
    private String name;
    private String description;
    private String verifyLink;

    public Badge(String image, String name, String description, String verifyLink) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.verifyLink = verifyLink;    
    }

    public String getVerifyLink() {
        return verifyLink;
    }

    public void setVerifyLink(String verifyLink) {
        this.verifyLink = verifyLink;
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
