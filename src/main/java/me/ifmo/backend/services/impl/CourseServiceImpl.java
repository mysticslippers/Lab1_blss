package me.ifmo.backend.services.impl;

import me.ifmo.backend.entities.Course;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.CourseRepository;
import me.ifmo.backend.services.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course getActiveCourseOrThrow(Long courseId) {
        return courseRepository.findByIdAndIsActiveTrue(courseId)
                .orElseThrow(() -> new NotFoundException("Active course not found: " + courseId));
    }

    @Override
    public List<Course> getAllActiveCourses() {
        return courseRepository.findAllByIsActiveTrue();
    }
}