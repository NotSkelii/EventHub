package com.skeli.searchservice.repository;

import com.skeli.searchservice.entity.EventDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String>{

    List<EventDocument> findByNameContainingOrDescriptionContainingOrVenueContainingOrCityContaining(
            String name, String description, String venue, String city
    );

    @Query("{\"bool\": {\"should\": [{\"match\": {\"name\": \"?0\"}}, {\"match\": {\"description\": \"?0\"}}," +
            "{\"match\": {\"venue\": \"?0\"}}, {\"match\": {\"city\": \"?0\"}}]}} ")
    List<EventDocument> findByKeyword(String keyword);

    List<EventDocument> findByCategory(String category);

    List<EventDocument> findByCity(String city);
}
