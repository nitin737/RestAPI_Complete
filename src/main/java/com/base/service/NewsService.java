package com.base.service;

import com.base.shared.dto.NewsArticleDto;
import java.util.List;

public interface NewsService {
    List<NewsArticleDto> getNews(
            String query,
            String language,
            String country,
            String fromDate,
            String toDate,
            Integer pageSize,
            Integer pageNum,
            String sortBy,
            String searchIn
    );
}
