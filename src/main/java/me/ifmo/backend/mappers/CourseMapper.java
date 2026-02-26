package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.course.CourseDTO;
import me.ifmo.backend.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDto(Course course);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toEntity(CourseDTO dto);
}