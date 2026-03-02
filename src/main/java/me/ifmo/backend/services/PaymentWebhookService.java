package me.ifmo.backend.services;

import me.ifmo.backend.DTO.webhook.PaymentWebhookDTO;

public interface PaymentWebhookService {

    void handlePaymentWebhook(PaymentWebhookDTO dto);
}