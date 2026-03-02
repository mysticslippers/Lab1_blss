package me.ifmo.backend.services.impl;

import me.ifmo.backend.entities.*;
import me.ifmo.backend.exceptions.ConflictException;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.UserRepository;
import me.ifmo.backend.services.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void ensureNoOpenEnrollment(Long userId, Long courseId) {
        enrollmentRepository.findByUser_IdAndCourse_IdAndStatusIn(
                userId,
                courseId,
                List.of(EnrollmentStatus.PENDING_PAYMENT, EnrollmentStatus.ACTIVE)
        ).ifPresent(e -> {
            throw new ConflictException("Enrollment already exists with status=" + e.getStatus());
        });
    }

    @Override
    public Enrollment createPending(Long userId, Course course) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.PENDING_PAYMENT)
                .rejectReason(null)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment getOrThrow(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));
    }

    @Override
    public Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId) {
        return enrollmentRepository.findByUser_IdAndCourse_Id(userId, courseId);
    }
}