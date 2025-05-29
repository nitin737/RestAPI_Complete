package com.base.io.repositories;

import com.base.io.entity.NewsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends PagingAndSortingRepository<NewsEntity, Long> {

    NewsEntity findByNewsId(String newsId);

    // The following query is a placeholder and will likely need to be adjusted
    // based on the specific database system and its geospatial capabilities.
    // For PostgreSQL with PostGIS, it might look something like:
    // @Query(value = "SELECT * FROM news n WHERE ST_DWithin(ST_MakePoint(n.longitude, n.latitude)::geography, ST_MakePoint(:longitude, :latitude)::geography, :radius * 1000)", nativeQuery = true)
    // For H2/MySQL, a simpler Haversine formula might be needed if not directly supported.
    @Query("SELECT n FROM news n WHERE " +
           "( 6371 * acos( cos( radians(:latitude) ) * cos( radians( n.latitude ) ) * " +
           "cos( radians(n.longitude) - radians(:longitude) ) + sin( radians(:latitude) ) * " +
           "sin( radians(n.latitude) ) ) ) < :radius")
    Page<NewsEntity> findNewsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius, // Assuming radius is in kilometers
            Pageable pageable
    );
}
