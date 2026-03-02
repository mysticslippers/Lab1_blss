package me.ifmo.backend.services;

import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;

import java.util.Optional;

public interface EnrollmentService {

    void ensureNoOpenEnrollment(Long userId, Long courseId);

    Enrollment createPending(Long userId, Course course);

    Enrollment getOrThrow(Long enrollmentId);

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}