package me.ifmo.backend.DTO.payment;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ifmo.backend.entities.enums.PaymentStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitResponse {

    @NotNull(message = "PaymentInitResponse.paymentId must not be null")
    private Long paymentId;

    @NotNull(message = "PaymentInitResponse.status must not be null")
    private PaymentStatus status;

    private String paymentLink;

    private OffsetDateTime expiresAt;

    private String providerPaymentId;
}