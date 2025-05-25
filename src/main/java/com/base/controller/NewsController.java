package com.base.controller;

import com.base.service.NewsService;
import com.base.shared.dto.NewsArticleDto;
import com.base.ui.model.response.NewsArticleRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/news") // e.g., http://localhost:8080/mobile-app-ws/news
public class NewsController {

    private final NewsService newsService;
    private final ModelMapper modelMapper;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
        this.modelMapper = new ModelMapper();
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<NewsArticleRest> getNews(
            @RequestParam(value = "q") String query,
            @RequestParam(value = "lang", required = false) String language,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "from", required = false) String fromDate,
            @RequestParam(value = "to", required = false) String toDate,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "in", required = false) String searchIn
    ) {
        List<NewsArticleDto> newsArticlesDto = newsService.getNews(
                query, language, country, fromDate, toDate, pageSize, pageNum, sortBy, searchIn
        );

        List<NewsArticleRest> returnValue = new ArrayList<>();
        if (newsArticlesDto != null && !newsArticlesDto.isEmpty()) {
            Type listType = new TypeToken<List<NewsArticleRest>>() {}.getType();
            returnValue = modelMapper.map(newsArticlesDto, listType);
        }
        return returnValue;
    }
}
