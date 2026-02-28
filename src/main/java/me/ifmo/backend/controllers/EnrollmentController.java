package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentRequest;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentResponse;
import me.ifmo.backend.DTO.enrollment.EnrollmentStatusResponse;
import me.ifmo.backend.services.EnrollmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
@Validated
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public CreateEnrollmentResponse createEnrollment(
            @RequestParam("userId") @NotNull @Positive Long userId,
            @Valid @RequestBody CreateEnrollmentRequest request
    ) {
        return enrollmentService.createEnrollment(userId, request);
    }

    @GetMapping("/{enrollmentId}/status")
    public EnrollmentStatusResponse getStatus(
            @PathVariable("enrollmentId") @NotNull @Positive Long enrollmentId
    ) {
        return enrollmentService.getStatus(enrollmentId);
    }
}