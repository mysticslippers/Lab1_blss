package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.enrollment.EnrollmentDTO;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "courseId", source = "course.id")
    EnrollmentDTO toDto(Enrollment enrollment);

    @Mapping(target = "user", expression = "java(userFromId(dto.getUserId()))")
    @Mapping(target = "course", expression = "java(courseFromId(dto.getCourseId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Enrollment toEntity(EnrollmentDTO dto);

    default User userFromId(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }

    default Course courseFromId(Long id) {
        if (id == null) return null;
        Course course = new Course();
        course.setId(id);
        return course;
    }
}