package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.CourseAccess;
import me.ifmo.backend.entities.enums.AccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface CourseAccessRepository extends JpaRepository<CourseAccess, Long> {

    Optional<CourseAccess> findByUser_IdAndCourse_Id(Long userId, Long courseId);

    Optional<CourseAccess> findByUser_IdAndCourse_IdAndStatus(Long userId, Long courseId, AccessStatus status);

    boolean existsByUser_IdAndCourse_IdAndStatus(Long userId, Long courseId, AccessStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT courseAccess FROM CourseAccess courseAccess WHERE courseAccess.user.id = :userId AND courseAccess.course.id = :courseId")
    Optional<CourseAccess> findByUserAndCourseForUpdate(@Param("userId") Long userId, @Param("courseId") Long courseId);
}