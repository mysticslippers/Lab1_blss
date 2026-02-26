package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.access.CourseAccessDTO;
import me.ifmo.backend.entities.CourseAccess;
import me.ifmo.backend.mappers.id.CourseIdMapper;
import me.ifmo.backend.mappers.id.UserIdMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserIdMapper.class, CourseIdMapper.class})
public interface CourseAccessMapper {

    @Mapping(target = "userId", source = "user")
    @Mapping(target = "courseId", source = "course")
    CourseAccessDTO toDto(CourseAccess access);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "course", source = "courseId")
    @Mapping(target = "grantedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseAccess toEntity(CourseAccessDTO dto);
}