package me.ifmo.backend.DTO.responses;

import me.ifmo.backend.entities.EnrollmentStatus;

public record EnrollResponseDTO(
        Long enrollmentId,
        EnrollmentStatus status,
        String paymentUrl,
        String message
) {}