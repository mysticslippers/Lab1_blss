package me.ifmo.backend.DTO.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequestDTO(
        @NotBlank String merchantPaymentRef,
        @NotNull @Positive Integer amountCents,
        @NotBlank String currency,
        @NotBlank String callbackUrl
){}
