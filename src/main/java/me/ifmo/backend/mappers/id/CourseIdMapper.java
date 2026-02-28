package me.ifmo.backend.mappers.id;

import me.ifmo.backend.entities.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseIdMapper {

    default Long toId(Course course) {
        return course == null ? null : course.getId();
    }

    default Course fromId(Long id) {
        if (id == null) return null;
        Course course = new Course();
        course.setId(id);
        return course;
    }
}