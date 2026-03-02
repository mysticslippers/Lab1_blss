package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import me.ifmo.backend.DTO.webhook.PaymentWebhookDTO;
import me.ifmo.backend.services.PaymentWebhookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    public PaymentWebhookController(PaymentWebhookService paymentWebhookService) {
        this.paymentWebhookService = paymentWebhookService;
    }

    @PostMapping(
            path = "/webhook",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> webhook(@Valid @RequestBody PaymentWebhookDTO dto) {
        paymentWebhookService.handlePaymentWebhook(dto);
        return ResponseEntity.noContent().build();
    }
}