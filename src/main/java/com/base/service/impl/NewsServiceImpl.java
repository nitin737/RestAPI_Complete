package com.base.service.impl;

import com.base.security.AppProperties;
import com.base.service.NewsService;
import com.base.shared.dto.NewsArticleDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = Logger.getLogger(NewsServiceImpl.class.getName());
    private static final String GNEWS_API_BASE_URL = "https://gnews.io/api/v4/search?";
    private final AppProperties appProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NewsServiceImpl(AppProperties appProperties, HttpClient httpClient) {
        this.appProperties = appProperties;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Cacheable(cacheNames = "newsCache")
    public List<NewsArticleDto> getNews(
            String query,
            String language,
            String country,
            String fromDate,
            String toDate,
            Integer pageSize,
            Integer pageNum,
            String sortBy,
            String searchIn) {

        String apiKey = appProperties.getGnewsApiKey();
        if (apiKey == null || apiKey.isEmpty() || "PLEASE_ENTER_YOUR_GNEWS_API_KEY".equals(apiKey)) {
            LOGGER.log(Level.SEVERE, "GNews API key is not configured. Please set gnews.api.key in application.properties.");
            return Collections.emptyList();
        }

        StringBuilder urlBuilder = new StringBuilder(GNEWS_API_BASE_URL);
        urlBuilder.append("apikey=").append(apiKey);

        try {
            urlBuilder.append("&q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Error encoding query parameter", e);
            return Collections.emptyList(); // Or handle more gracefully
        }

        appendQueryParam(urlBuilder, "lang", language);
        appendQueryParam(urlBuilder, "country", country);
        appendQueryParam(urlBuilder, "from", fromDate);
        appendQueryParam(urlBuilder, "to", toDate);

        // GNews free tier max is 10 for 'max' parameter (pageSize)
        int effectivePageSize = (pageSize == null || pageSize <= 0 || pageSize > 10) ? 10 : pageSize;
        appendQueryParam(urlBuilder, "max", String.valueOf(effectivePageSize));


        if (pageNum != null && pageNum > 0) { // pageNum is a paid feature
            appendQueryParam(urlBuilder, "page", String.valueOf(pageNum));
        }
        appendQueryParam(urlBuilder, "sortby", sortBy);
        appendQueryParam(urlBuilder, "in", searchIn);

        String apiUrl = urlBuilder.toString();
        LOGGER.log(Level.INFO, "Requesting GNews API: " + apiUrl);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode articlesNode = root.get("articles");
                List<NewsArticleDto> articles = new ArrayList<>();
                if (articlesNode != null && articlesNode.isArray()) {
                    for (JsonNode articleNode : articlesNode) {
                        String title = articleNode.has("title") ? articleNode.get("title").asText() : null;
                        String description = articleNode.has("description") ? articleNode.get("description").asText() : null;
                        String url = articleNode.has("url") ? articleNode.get("url").asText() : null;
                        articles.add(new NewsArticleDto(title, description, url));
                    }
                    return articles;
                } else {
                    LOGGER.log(Level.WARNING, "GNews API response does not contain 'articles' array or it's null. Response: " + response.body());
                    return Collections.emptyList();
                }
            } else {
                logGNewsError(statusCode, response.body());
                return Collections.emptyList();
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "Error fetching news from GNews API: " + apiUrl, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Restore interruption status
            }
            return Collections.emptyList();
        }
    }

    private void appendQueryParam(StringBuilder urlBuilder, String paramName, String paramValue) {
        if (paramValue != null && !paramValue.isEmpty()) {
            try {
                // GNews documentation doesn't explicitly state all params need URL encoding
                // but it's safer for values that might contain special characters.
                // For params like 'lang', 'country', 'sortby', 'in' which expect specific values,
                // encoding might not be strictly necessary but doesn't hurt.
                urlBuilder.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                LOGGER.log(Level.WARNING, "Error encoding parameter: " + paramName, e);
                // Decide if you want to skip this param or handle error differently
            }
        }
    }

    private void logGNewsError(int statusCode, String responseBody) {
        String commonMessage = "GNews API request failed with status code: " + statusCode;
        String detailedMessage = responseBody != null ? ". Response body: " + responseBody : "";

        switch (statusCode) {
            case 400:
                LOGGER.log(Level.WARNING, commonMessage + " (Bad Request)" + detailedMessage);
                break;
            case 401:
                LOGGER.log(Level.WARNING, commonMessage + " (Unauthorized - Check API Key)" + detailedMessage);
                break;
            case 403:
                LOGGER.log(Level.WARNING, commonMessage + " (Forbidden - Quota likely reached or API key permissions issue)" + detailedMessage);
                break;
            case 429:
                LOGGER.log(Level.WARNING, commonMessage + " (Too Many Requests)" + detailedMessage);
                break;
            case 500:
                LOGGER.log(Level.WARNING, commonMessage + " (Internal Server Error on GNews side)" + detailedMessage);
                break;
            case 503:
                LOGGER.log(Level.WARNING, commonMessage + " (Service Unavailable on GNews side)" + detailedMessage);
                break;
            default:
                LOGGER.log(Level.WARNING, commonMessage + detailedMessage);
        }
    }
}
