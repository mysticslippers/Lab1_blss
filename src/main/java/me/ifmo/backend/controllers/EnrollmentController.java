package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import me.ifmo.backend.DTO.requests.EnrollRequestDTO;
import me.ifmo.backend.DTO.responses.EnrollResponseDTO;
import me.ifmo.backend.DTO.responses.EnrollmentInfoResponseDTO;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.mappers.EnrollmentMapper;
import me.ifmo.backend.services.EnrollmentService;
import me.ifmo.backend.services.orchestrator.EnrollmentOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentOrchestrator enrollmentOrchestrator;
    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentController(
            EnrollmentOrchestrator enrollmentOrchestrator,
            EnrollmentService enrollmentService,
            EnrollmentMapper enrollmentMapper
    ) {
        this.enrollmentOrchestrator = enrollmentOrchestrator;
        this.enrollmentService = enrollmentService;
        this.enrollmentMapper = enrollmentMapper;
    }

    @PostMapping
    public ResponseEntity<EnrollResponseDTO> enroll(@Valid @RequestBody EnrollRequestDTO request) {
        return ResponseEntity.ok(enrollmentOrchestrator.enroll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentInfoResponseDTO> getById(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.getOrThrow(id);
        return ResponseEntity.ok(enrollmentMapper.toEnrollmentInfoResponseDto(enrollment));
    }
}