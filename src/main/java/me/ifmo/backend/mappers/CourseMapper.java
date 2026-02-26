package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.course.CourseDTO;
import me.ifmo.backend.entities.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDto(Course course);

    Course toEntity(CourseDTO dto);
}