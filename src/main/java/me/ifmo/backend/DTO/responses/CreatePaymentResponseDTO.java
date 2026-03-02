package me.ifmo.backend.DTO.responses;

public record CreatePaymentResponseDTO (
        String providerPaymentId,
        String paymentUrl
){}
