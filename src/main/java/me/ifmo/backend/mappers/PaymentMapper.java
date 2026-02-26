package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.payment.PaymentDTO;
import me.ifmo.backend.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EnrollmentIdMapper.class})
public interface PaymentMapper {

    @Mapping(target = "enrollmentId", source = "enrollment")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "enrollment", source = "enrollmentId")
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentDTO dto);
}