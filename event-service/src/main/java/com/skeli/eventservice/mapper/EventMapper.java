package com.skeli.eventservice.mapper;

import com.skeli.eventservice.dto.EventRequestDto;
import com.skeli.eventservice.dto.EventResponseDto;
import com.skeli.eventservice.entity.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventResponseDto toResponseDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableSeats", expression = "java(request.getTotalSeats())")
    @Mapping(target = "organizerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "imageUrls", source = "imageUrls")
    Event toEntity(EventRequestDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Event event, EventRequestDto request);
}
