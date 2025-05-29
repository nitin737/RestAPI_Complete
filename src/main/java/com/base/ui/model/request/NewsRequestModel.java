package com.base.ui.model.request;

// You might want to add validation annotations here (e.g., @NotNull, @Size)
// import javax.validation.constraints.NotBlank; // Example
// import javax.validation.constraints.Size; // Example
// import javax.validation.constraints.Min; // Example
// import javax.validation.constraints.Max; // Example


public class NewsRequestModel {

    // @NotBlank(message = "Title cannot be blank")
    // @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    // @NotBlank(message = "Content cannot be blank")
    private String content;

    // @Min(value = -90, message = "Latitude must be between -90 and 90")
    // @Max(value = 90, message = "Latitude must be between -90 and 90")
    private double latitude;

    // @Min(value = -180, message = "Longitude must be between -180 and 180")
    // @Max(value = 180, message = "Longitude must be between -180 and 180")
    private double longitude;

    // Getters and Setters
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
