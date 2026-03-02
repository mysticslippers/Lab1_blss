package me.ifmo.backend.DTO.responses;

import java.time.Instant;

public record ApiErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}
