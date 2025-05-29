package com.base.service.impl;

import com.base.io.entity.NewsEntity;
import com.base.io.repositories.NewsRepository;
import com.base.service.NewsService;
import com.base.shared.Utils; // Assuming Utils class for generatePublicId
import com.base.shared.dto.NewsDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final Utils utils; // For generating public newsId
    private final ModelMapper modelMapper;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, Utils utils, ModelMapper modelMapper) {
        this.newsRepository = newsRepository;
        this.utils = utils;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<NewsDto> getNews(double latitude, double longitude, double radius, int page, int limit) {
        if (page > 0) {
            page = page - 1; // Pageable is 0-indexed
        }

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<NewsEntity> newsPage = newsRepository.findNewsWithinRadius(latitude, longitude, radius, pageableRequest);
        List<NewsEntity> newsEntities = newsPage.getContent();

        Type listType = new TypeToken<List<NewsDto>>() {}.getType();
        return modelMapper.map(newsEntities, listType);
    }

    @Override
    public NewsDto createNews(NewsDto newsDto) {
        NewsEntity newsEntity = modelMapper.map(newsDto, NewsEntity.class);

        // Generate a public newsId
        String publicNewsId = utils.generatePublicId(30); // Assuming similar length to userId
        newsEntity.setNewsId(publicNewsId);

        NewsEntity storedNewsEntity = newsRepository.save(newsEntity);
        return modelMapper.map(storedNewsEntity, NewsDto.class);
    }

    @Override
    public NewsDto getNewsById(String newsId) {
        NewsEntity newsEntity = newsRepository.findByNewsId(newsId);
        if (newsEntity == null) {
            // Consider throwing a custom exception e.g., NewsNotFoundException
            return null;
        }
        return modelMapper.map(newsEntity, NewsDto.class);
    }
}
