package com.base.service.impl;

import com.base.security.AppProperties;
import com.base.shared.dto.NewsArticleDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private AppProperties mockAppProperties;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private NewsServiceImpl newsService;

    private final String DUMMY_API_KEY = "TEST_API_KEY";

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(mockAppProperties, mockHttpClient);
        // Common setup: Assume API key is valid for most tests
        when(mockAppProperties.getGnewsApiKey()).thenReturn(DUMMY_API_KEY);
    }

    @Test
    void getNews_whenApiKeyMissing_returnsEmptyList() {
        when(mockAppProperties.getGnewsApiKey()).thenReturn(null);
        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());

        when(mockAppProperties.getGnewsApiKey()).thenReturn("PLEASE_ENTER_YOUR_GNEWS_API_KEY");
        result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNews_successfulApiCall_returnsParsedArticles() throws IOException, InterruptedException {
        String jsonResponse = "{\n" +
                "    \"totalArticles\": 1,\n" +
                "    \"articles\": [\n" +
                "        {\n" +
                "            \"title\": \"Test Title\",\n" +
                "            \"description\": \"Test Description\",\n" +
                "            \"url\": \"http://example.com/news\",\n" +
                "            \"content\": \"Full content here\",\n" +
                "            \"image\": \"http://example.com/image.jpg\",\n" +
                "            \"publishedAt\": \"2023-01-01T12:00:00Z\",\n" +
                "            \"source\": {\"name\": \"Test Source\", \"url\": \"http://example.com\"}\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("testQuery", null, null, null, null, 10, 1, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        NewsArticleDto article = result.get(0);
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Description", article.getDescription());
        assertEquals("http://example.com/news", article.getUrl());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertTrue(capturedRequest.uri().toString().contains("q=testQuery"));
        assertTrue(capturedRequest.uri().toString().contains("apikey=" + DUMMY_API_KEY));
        assertTrue(capturedRequest.uri().toString().contains("max=10"));
        assertTrue(capturedRequest.uri().toString().contains("page=1"));
    }

    @Test
    void getNews_urlConstruction_withAllParameters() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("{\"totalArticles\": 0, \"articles\": []}"); // Empty articles for URL test
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        newsService.getNews("topic", "en", "us", "2023-01-01", "2023-01-31", 5, 2, "publishedAt", "title,description");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        String requestUrl = requestCaptor.getValue().uri().toString();

        assertTrue(requestUrl.contains("q=topic"));
        assertTrue(requestUrl.contains("lang=en"));
        assertTrue(requestUrl.contains("country=us"));
        assertTrue(requestUrl.contains("from=2023-01-01"));
        assertTrue(requestUrl.contains("to=2023-01-31"));
        assertTrue(requestUrl.contains("max=5")); // PageSize 5
        assertTrue(requestUrl.contains("page=2")); // PageNum 2
        assertTrue(requestUrl.contains("sortby=publishedAt"));
        assertTrue(requestUrl.contains("in=title%2Cdescription")); // URL encoded
    }

    @Test
    void getNews_urlConstruction_withSomeNullParameters() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("{\"totalArticles\": 0, \"articles\": []}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        newsService.getNews("topic", "fr", null, "2023-02-01", null, 7, null, null, "title");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        String requestUrl = requestCaptor.getValue().uri().toString();

        assertTrue(requestUrl.contains("q=topic"));
        assertTrue(requestUrl.contains("lang=fr"));
        assertFalse(requestUrl.contains("country="));
        assertTrue(requestUrl.contains("from=2023-02-01"));
        assertFalse(requestUrl.contains("to="));
        assertTrue(requestUrl.contains("max=7")); // pageSize 7
        assertFalse(requestUrl.contains("page=")); // pageNum null
        assertFalse(requestUrl.contains("sortby=")); // sortBy null
        assertTrue(requestUrl.contains("in=title"));
    }
    
    @Test
    void getNews_urlConstruction_pageSizeZeroOrNegative_defaultsToTen() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("{\"totalArticles\": 0, \"articles\": []}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        newsService.getNews("topic", null, null, null, null, 0, null, null, null);
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        assertTrue(requestCaptor.getValue().uri().toString().contains("max=10"));
        
        newsService.getNews("topic", null, null, null, null, -5, null, null, null);
        verify(mockHttpClient, times(2)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        assertTrue(requestCaptor.getValue().uri().toString().contains("max=10"));

        newsService.getNews("topic", null, null, null, null, null, null, null, null);
        verify(mockHttpClient, times(3)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        assertTrue(requestCaptor.getValue().uri().toString().contains("max=10"));
    }


    @Test
    void getNews_apiError401_returnsEmptyList() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(401);
        when(mockHttpResponse.body()).thenReturn("{\"errors\": [\"Unauthorized\"]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNews_apiError403_returnsEmptyList() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(403);
        when(mockHttpResponse.body()).thenReturn("{\"errors\": [\"Forbidden\"]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNews_apiError500_returnsEmptyList() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpResponse.body()).thenReturn("{\"errors\": [\"Internal Server Error\"]}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNews_networkIOException_returnsEmptyList() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("Network error"));

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void getNews_networkInterruptedException_returnsEmptyListAndRestoresInterrupt() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new InterruptedException("Request interrupted"));

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
        assertTrue(Thread.currentThread().isInterrupted(), "Interruption status should be restored");
        // Clear interrupt for subsequent tests if run in the same thread pool (though JUnit usually isolates)
        Thread.interrupted(); 
    }

    @Test
    void getNews_malformedJsonResponse_returnsEmptyList() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("This is not valid JSON {");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNews_responseMissingArticlesField_returnsEmptyList() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("{\"totalArticles\": 0}"); // No "articles" field
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void getNews_articlesFieldIsNull_returnsEmptyList() throws IOException, InterruptedException {
        String jsonResponse = "{\"totalArticles\": 1, \"articles\": null}";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("test", null, null, null, null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNews_articleNodeMissingFields_parsesWithNulls() throws IOException, InterruptedException {
        String jsonResponse = "{\n" +
                "    \"totalArticles\": 1,\n" +
                "    \"articles\": [\n" +
                "        {\n" +
                "            \"title\": \"Test Title Only\"\n" + // Only title, description and url are missing
                "        }\n" +
                "    ]\n" +
                "}";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<NewsArticleDto> result = newsService.getNews("testQuery", null, null, null, null, null, null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        NewsArticleDto article = result.get(0);
        assertEquals("Test Title Only", article.getTitle());
        assertNull(article.getDescription());
        assertNull(article.getUrl());
    }
}
