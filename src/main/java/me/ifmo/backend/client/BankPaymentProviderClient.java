package me.ifmo.backend.client;

import me.ifmo.backend.DTO.requests.CreatePaymentRequestDTO;
import me.ifmo.backend.DTO.responses.CreatePaymentResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BankPaymentProviderClient implements PaymentProviderClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public BankPaymentProviderClient(@Value("${bank.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public CreatePaymentResponseDTO createPayment(CreatePaymentRequestDTO request) {
        String url = baseUrl + "/api/payments";
        return restTemplate.postForObject(url, request, CreatePaymentResponseDTO.class);
    }
}