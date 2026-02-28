package me.ifmo.backend.DTO.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachProviderDetailsRequest {

    @NotBlank(message = "AttachProviderDetailsRequest.providerPaymentId must not be blank")
    private String providerPaymentId;

    private String paymentLink;
}