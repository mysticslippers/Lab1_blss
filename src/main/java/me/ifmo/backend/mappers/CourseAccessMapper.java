package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.access.CourseAccessDTO;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.CourseAccess;
import me.ifmo.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseAccessMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "courseId", source = "course.id")
    CourseAccessDTO toDto(CourseAccess access);

    @Mapping(target = "user", expression = "java(userFromId(dto.getUserId()))")
    @Mapping(target = "course", expression = "java(courseFromId(dto.getCourseId()))")
    @Mapping(target = "grantedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseAccess toEntity(CourseAccessDTO dto);

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