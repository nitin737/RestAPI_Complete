package com.base.io.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name="news")
public class NewsEntity implements Serializable {

    private static final long serialVersionUID = 1L; // Or any other unique long value

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable=false, unique=true)
    private String newsId; // Public unique ID

    @Column(nullable=false)
    private String title;

    @Column(nullable=false, length=2000) // Assuming content can be long
    private String content;

    @Column(nullable=false)
    private double latitude;

    @Column(nullable=false)
    private double longitude;

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
