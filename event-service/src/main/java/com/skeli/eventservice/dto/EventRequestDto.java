package com.skeli.eventservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100, message = "Event name must be between 3 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed than 2000 characters")
    private String description;

    @NotBlank(message = "Venue cannot be blank")
    private String venue;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotNull(message = "Event date cannot be blank")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @NotNull(message = "Total seats cannot be blank")
    @Min(value = 1, message = "Total seats must be greater than 0")
    private Integer totalSeats;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private List<String> imageUrls;
}
