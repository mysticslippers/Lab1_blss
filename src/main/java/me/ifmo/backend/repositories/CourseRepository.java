package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByIdAndIsActiveTrue(Long id);

    List<Course> findAllByIsActiveTrue();
}
