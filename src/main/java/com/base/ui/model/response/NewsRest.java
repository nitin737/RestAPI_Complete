package com.base.ui.model.response;

// No HATEOAS for this simple model, but could be added if needed
// import org.springframework.hateoas.RepresentationModel;

public class NewsRest { // Extends RepresentationModel<NewsRest> if using HATEOAS

    private String newsId;
    private String title;
    private String content;
    private double latitude;
    private double longitude;

    // Getters and Setters
    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
