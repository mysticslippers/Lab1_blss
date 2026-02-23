package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByEnrollmentOpenTrueAndStatus(CourseStatus status);

    boolean existsByIdAndEnrollmentOpenTrueAndStatus(Long id, CourseStatus status);
}