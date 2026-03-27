package me.ifmo.backend.integration.bank;

import me.ifmo.backend.integration.bank.DTO.BankPaymentRequest;
import me.ifmo.backend.integration.bank.DTO.BankPaymentResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockBankClientImpl implements BankClient {

    @Override
    public BankPaymentResponse createPayment(BankPaymentRequest request) {
        return BankPaymentResponse.builder()
                .providerPaymentId(UUID.randomUUID().toString())
                .paymentUrl("http://localhost:8081/mock-bank/payments/" + UUID.randomUUID())
                .build();
    }
}