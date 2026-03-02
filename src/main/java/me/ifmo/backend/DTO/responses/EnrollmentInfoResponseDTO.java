package me.ifmo.backend.DTO.responses;

import me.ifmo.backend.entities.EnrollmentStatus;

public record EnrollmentInfoResponseDTO(
        Long id,
        Long userId,
        Long courseId,
        EnrollmentStatus status,
        String rejectReason
) {}
