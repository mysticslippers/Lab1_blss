package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUser_IdAndCourse_Id(Long userId, Long courseId);

    Optional<Enrollment> findByUser_IdAndCourse_IdAndStatusIn(
            Long userId,
            Long courseId,
            Collection<EnrollmentStatus> statuses
    );
}
