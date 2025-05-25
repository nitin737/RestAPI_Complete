package com.base.shared.dto;

public class NewsArticleDto {
    // Fields will be added in the next step
    // For now, this is a placeholder
    private String title;
    private String description;
    private String url;

    public NewsArticleDto(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
