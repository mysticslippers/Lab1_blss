package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.responses.CourseResponseDTO;
import me.ifmo.backend.entities.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponseDTO toCourseResponseDto(Course course);
}