package com.base.service;

import com.base.shared.dto.NewsDto;
import java.util.List;

public interface NewsService {
    /**
     * Retrieves news items within a specified radius from a given latitude and longitude.
     * Supports pagination.
     *
     * @param latitude User's or center point's latitude.
     * @param longitude User's or center point's longitude.
     * @param radius Radius in kilometers.
     * @param page Page number for pagination (0-indexed).
     * @param limit Number of items per page.
     * @return A list of NewsDto objects.
     */
    List<NewsDto> getNews(double latitude, double longitude, double radius, int page, int limit);

    /**
     * Creates a new news item.
     * (This is a common operation, adding it here for completeness,
     *  though not explicitly requested in the "get news by radius" feature)
     * @param newsDto Data for the new news item.
     * @return The created NewsDto.
     */
    NewsDto createNews(NewsDto newsDto);

    /**
     * Gets a news item by its public ID.
     * @param newsId The public ID of the news item.
     * @return The NewsDto or null if not found.
     */
    NewsDto getNewsById(String newsId);

    // Potentially other methods like updateNews, deleteNews could be added later.
}
