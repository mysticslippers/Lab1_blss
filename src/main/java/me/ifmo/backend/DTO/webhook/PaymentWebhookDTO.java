package me.ifmo.backend.DTO.webhook;

import jakarta.validation.constraints.NotBlank;

public record PaymentWebhookDTO(
        @NotBlank String providerPaymentId,
        @NotBlank String merchantPaymentRef,
        @NotBlank String status,
        String payload
) {}
