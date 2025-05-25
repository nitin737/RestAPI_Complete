package com.base.controller;

import com.base.service.NewsService;
import com.base.shared.dto.NewsArticleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    // ModelMapper is instantiated directly in NewsController, so no need to mock it here.

    @Test
    void getNews_basicRequest_shouldReturnNewsArticles() throws Exception {
        NewsArticleDto articleDto = new NewsArticleDto("Test Title", "Test Desc", "http://example.com/news");
        List<NewsArticleDto> newsListDto = Arrays.asList(articleDto);

        // Parameters: query, language, country, fromDate, toDate, pageSize, pageNum, sortBy, searchIn
        when(newsService.getNews(
                eq("test-query"), // q
                isNull(),        // lang
                isNull(),        // country
                isNull(),        // from
                isNull(),        // to
                eq(10),          // pageSize (default)
                eq(1),           // pageNum (default for 'page' param)
                isNull(),        // sortBy (for 'sort' param)
                isNull()         // searchIn
        )).thenReturn(newsListDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .param("q", "test-query")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].description").value("Test Desc"))
                .andExpect(jsonPath("$[0].url").value("http://example.com/news"));

        verify(newsService).getNews(
                eq("test-query"),
                isNull(), isNull(), isNull(), isNull(),
                eq(10), eq(1),
                isNull(), isNull()
        );
    }

    @Test
    void getNews_withAllParameters_shouldReturnNewsArticles() throws Exception {
        NewsArticleDto articleDto = new NewsArticleDto("Full Test", "Full Desc", "http://example.com/full");
        List<NewsArticleDto> newsListDto = Arrays.asList(articleDto);

        when(newsService.getNews(
                eq("all-params-query"),
                eq("en"),
                eq("us"),
                eq("2023-01-01"),
                eq("2023-01-31"),
                eq(5),
                eq(2),
                eq("publishedAt"),
                eq("title,description")
        )).thenReturn(newsListDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .param("q", "all-params-query")
                .param("lang", "en")
                .param("country", "us")
                .param("from", "2023-01-01")
                .param("to", "2023-01-31")
                .param("pageSize", "5")
                .param("page", "2")
                .param("sort", "publishedAt")
                .param("in", "title,description")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Full Test"))
                .andExpect(jsonPath("$[0].description").value("Full Desc"))
                .andExpect(jsonPath("$[0].url").value("http://example.com/full"));

        verify(newsService).getNews(
                eq("all-params-query"),
                eq("en"),
                eq("us"),
                eq("2023-01-01"),
                eq("2023-01-31"),
                eq(5),
                eq(2),
                eq("publishedAt"),
                eq("title,description")
        );
    }

    @Test
    void getNews_missingMandatoryQueryParamQ_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // HTTP 400

        // Verify newsService.getNews is not called
        verify(newsService, times(0)).getNews(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getNews_newsServiceReturnsEmptyList_shouldReturnOkWithEmptyArray() throws Exception {
        when(newsService.getNews(
                eq("empty-query"),
                isNull(), isNull(), isNull(), isNull(),
                eq(10), eq(1),
                isNull(), isNull()
        )).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .param("q", "empty-query")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(newsService).getNews(
                eq("empty-query"),
                isNull(), isNull(), isNull(), isNull(),
                eq(10), eq(1),
                isNull(), isNull()
        );
    }

    @Test
    void getNews_invalidPageSizeParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .param("q", "test-query")
                .param("pageSize", "abc") // Invalid integer
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // HTTP 400

        verify(newsService, times(0)).getNews(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getNews_invalidPageParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/news")
                .param("q", "test-query")
                .param("page", "xyz") // Invalid integer
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // HTTP 400

        verify(newsService, times(0)).getNews(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }
}
