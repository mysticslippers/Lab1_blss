package me.ifmo.backend.DTO.responses;

public record CourseResponseDTO(
        Long id,
        String title,
        String description,
        Integer priceCents,
        String currency,
        Boolean isActive
) {}
