package me.ifmo.backend.mappers.id;

import me.ifmo.backend.entities.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentIdMapper {

    default Long toId(Payment payment) {
        return payment == null ? null : payment.getId();
    }

    default Payment fromId(Long id) {
        if (id == null) return null;
        Payment p = new Payment();
        p.setId(id);
        return p;
    }
}