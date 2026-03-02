package me.ifmo.backend.client;

import me.ifmo.backend.DTO.requests.CreatePaymentRequestDTO;
import me.ifmo.backend.DTO.responses.CreatePaymentResponseDTO;

public interface PaymentProviderClient {

    CreatePaymentResponseDTO createPayment(CreatePaymentRequestDTO request);
}