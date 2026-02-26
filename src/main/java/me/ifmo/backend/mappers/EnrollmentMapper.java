package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.enrollment.EnrollmentDTO;
import me.ifmo.backend.entities.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CourseMapper.class})
public interface EnrollmentMapper {

    @Mapping(target = "userId", source = "user")
    @Mapping(target = "courseId", source = "course")
    EnrollmentDTO toDto(Enrollment enrollment);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "course", source = "courseId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Enrollment toEntity(EnrollmentDTO dto);
}