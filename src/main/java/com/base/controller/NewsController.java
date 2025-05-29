package com.base.controller;

import com.base.service.NewsService;
import com.base.shared.dto.NewsDto;
import com.base.ui.model.response.NewsRest; // To be created in the next step
import com.base.ui.model.response.OperationStatusModel; // Assuming this exists for potential future POST/PUT operations
import com.base.ui.model.response.RequestOperationStatus; // Assuming this exists
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/news") // http://localhost:8080/mobile-app-ws/news
public class NewsController {

    private final NewsService newsService;
    private final ModelMapper modelMapper;

    @Autowired
    public NewsController(NewsService newsService, ModelMapper modelMapper) {
        this.newsService = newsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<NewsRest> getNews(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude,
            @RequestParam(value = "radius", defaultValue = "100") double radius, // Default radius 100km
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<NewsDto> newsDtos = newsService.getNews(latitude, longitude, radius, page, limit);

        Type listType = new TypeToken<List<NewsRest>>() {}.getType();
        return modelMapper.map(newsDtos, listType);
    }

    // Placeholder for getting a single news item by ID - can be fleshed out if needed
    @GetMapping(path = "/{newsId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public NewsRest getNewsById(@PathVariable String newsId) {
        NewsDto newsDto = newsService.getNewsById(newsId);
        if (newsDto == null) {
            // In a real app, you'd throw an exception here that gets mapped to 404
            // For now, returning null might result in an empty response or error depending on framework setup
            return null;
        }
        return modelMapper.map(newsDto, NewsRest.class);
    }
    
    // Placeholder for creating news - can be fleshed out if needed
    // For this, you would also need a NewsRequestModel.java
    /*
    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public NewsRest createNews(@RequestBody YourNewsRequestModel newsDetails) throws Exception { // Replace YourNewsRequestModel
        // ... (map request model to DTO)
        // NewsDto newsDto = modelMapper.map(newsDetails, NewsDto.class);
        // NewsDto createdNews = newsService.createNews(newsDto);
        // return modelMapper.map(createdNews, NewsRest.class);
        return null; // Placeholder
    }
    */
}
