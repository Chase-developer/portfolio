package com.chase.portfolio.models;
public class Badge {
    private final String image;
    private final String name;
    private final String description;
    private final String verifyLink;

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
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
    }
}
