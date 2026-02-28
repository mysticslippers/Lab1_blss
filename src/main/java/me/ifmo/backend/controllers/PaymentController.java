package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.payment.AttachProviderDetailsRequest;
import me.ifmo.backend.DTO.payment.CreatePaymentRequest;
import me.ifmo.backend.DTO.payment.PaymentInitResponse;
import me.ifmo.backend.services.PaymentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentInitResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @PostMapping("/{paymentId}/provider")
    public PaymentInitResponse attachProviderDetails(
            @PathVariable("paymentId") @NotNull @Positive Long paymentId,
            @Valid @RequestBody AttachProviderDetailsRequest request
    ) {
        return paymentService.attachProviderDetails(paymentId, request.getProviderPaymentId(), request.getPaymentLink());
    }
}