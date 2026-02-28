package me.ifmo.backend.services;

import me.ifmo.backend.DTO.payment.CreatePaymentRequest;
import me.ifmo.backend.DTO.payment.PaymentInitResponse;
import me.ifmo.backend.DTO.webhook.PaymentWebhookRequest;
import me.ifmo.backend.DTO.webhook.WebhookAckResponse;

public interface PaymentService {

    PaymentInitResponse createPayment(CreatePaymentRequest request);

    PaymentInitResponse attachProviderDetails(Long paymentId, String providerPaymentId, String paymentLink);

    WebhookAckResponse handleWebhook(PaymentWebhookRequest request);
}