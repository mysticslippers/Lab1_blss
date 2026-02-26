package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.refund.RefundDTO;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    @Mapping(target = "paymentId", source = "payment.id")
    RefundDTO toDto(Refund refund);

    @Mapping(target = "payment", expression = "java(paymentFromId(dto.getPaymentId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Refund toEntity(RefundDTO dto);

    default Payment paymentFromId(Long id) {
        if (id == null) return null;
        Payment payment = new Payment();
        payment.setId(id);
        return payment;
    }
}