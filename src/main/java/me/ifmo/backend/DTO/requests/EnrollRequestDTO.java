package me.ifmo.backend.DTO.requests;

import jakarta.validation.constraints.NotNull;

public record EnrollRequestDTO(
        @NotNull Long userId,
        @NotNull Long courseId
) {}