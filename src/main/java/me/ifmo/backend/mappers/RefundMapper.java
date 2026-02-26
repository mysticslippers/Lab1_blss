package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.refund.RefundDTO;
import me.ifmo.backend.entities.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PaymentIdMapper.class})
public interface RefundMapper {

    @Mapping(target = "paymentId", source = "payment")
    RefundDTO toDto(Refund refund);

    @Mapping(target = "payment", source = "paymentId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Refund toEntity(RefundDTO dto);
}