package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.webhook.PaymentWebhookRequest;
import me.ifmo.backend.DTO.webhook.WebhookAckResponse;
import me.ifmo.backend.services.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/minibank/payment")
    public WebhookAckResponse minibankPaymentWebhook(@Valid @RequestBody PaymentWebhookRequest request) {
        return paymentService.handleWebhook(request);
    }
}