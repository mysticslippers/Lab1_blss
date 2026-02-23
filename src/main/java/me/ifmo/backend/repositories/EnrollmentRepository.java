package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUser_IdAndCourse_Id(Long userId, Long courseId);

    Optional<Enrollment> findByUser_IdAndCourse_IdAndStatusIn(
            Long userId,
            Long courseId,
            Collection<EnrollmentStatus> statuses
    );

    boolean existsByUser_IdAndCourse_IdAndStatusIn(
            Long userId,
            Long courseId,
            Collection<EnrollmentStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT enrollment FROM Enrollment enrollment WHERE enrollment.id = :id")
    Optional<Enrollment> findByIdForUpdate(@Param("id") Long id);
}