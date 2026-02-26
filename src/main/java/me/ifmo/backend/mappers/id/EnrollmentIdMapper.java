package me.ifmo.backend.mappers.id;

import me.ifmo.backend.entities.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentIdMapper {

    default Long toId(Enrollment enrollment) {
        return enrollment == null ? null : enrollment.getId();
    }

    default Enrollment fromId(Long id) {
        if (id == null) return null;
        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        return enrollment;
    }
}