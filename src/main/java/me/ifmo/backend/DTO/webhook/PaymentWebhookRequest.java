package me.ifmo.backend.DTO.webhook;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PaymentWebhookRequest {

    @NotBlank(message = "PaymentWebhookRequest.eventId must not be blank")
    @Size(max = 128, message = "PaymentWebhookRequest.eventId must be at most 128 characters")
    private String eventId;

    @NotBlank(message = "PaymentWebhookRequest.eventType must not be blank")
    @Size(max = 64, message = "PaymentWebhookRequest.eventType must be at most 64 characters")
    private String eventType;

    @NotNull(message = "PaymentWebhookRequest.status must not be null")
    private PaymentStatus status;

    private OffsetDateTime paidAt;

    @Size(max = 512, message = "PaymentWebhookRequest.signature must be at most 512 characters")
    private String signature;

    @NotBlank(message = "PaymentWebhookRequest.providerPaymentId must not be blank")
    @Size(max = 128, message = "PaymentWebhookRequest.providerPaymentId must be at most 128 characters")
    private String providerPaymentId;
}