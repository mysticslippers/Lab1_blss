package me.ifmo.backend.controllers;

import me.ifmo.backend.DTO.responses.CourseResponseDTO;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.mappers.CourseMapper;
import me.ifmo.backend.services.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllActive() {
        List<CourseResponseDTO> result = courseService.getAllActiveCourses()
                .stream()
                .map(courseMapper::toCourseResponseDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getActiveById(@PathVariable Long id) {
        Course course = courseService.getActiveCourseOrThrow(id);
        return ResponseEntity.ok(courseMapper.toCourseResponseDto(course));
    }
}