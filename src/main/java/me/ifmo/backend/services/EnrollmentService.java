package me.ifmo.backend.services;

import me.ifmo.backend.DTO.enrollment.CreateEnrollmentRequest;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentResponse;
import me.ifmo.backend.DTO.enrollment.EnrollmentStatusResponse;

public interface EnrollmentService {

    CreateEnrollmentResponse createEnrollment(Long userId, CreateEnrollmentRequest request);

    EnrollmentStatusResponse getStatus(Long enrollmentId);
}