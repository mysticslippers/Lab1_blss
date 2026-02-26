package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.payment.PaymentDTO;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "enrollmentId", source = "enrollment.id")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "enrollment", expression = "java(enrollmentFromId(dto.getEnrollmentId()))")
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentDTO dto);

    default Enrollment enrollmentFromId(Long id) {
        if (id == null) return null;
        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        return enrollment;
    }
}