package me.ifmo.backend.services;

import me.ifmo.backend.entities.Course;

import java.util.List;

public interface CourseService {

    Course getActiveCourseOrThrow(Long courseId);

    List<Course> getAllActiveCourses();
}