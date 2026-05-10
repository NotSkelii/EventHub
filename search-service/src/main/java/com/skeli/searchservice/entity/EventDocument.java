package com.skeli.searchservice.entity;

import co.elastic.clients.elasticsearch._types.analysis.KeywordAnalyzer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "events")
public class EventDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String venue;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String city;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Date)
    private String eventDate;

    @Field(type = FieldType.Integer)
    private Integer totalSeats;

    @Field(type = FieldType.Integer)
    private Integer availableSeats;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String organizerId;
}
