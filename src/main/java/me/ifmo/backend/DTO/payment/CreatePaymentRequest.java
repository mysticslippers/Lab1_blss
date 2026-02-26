package me.ifmo.backend.DTO.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull(message = "CreatePaymentRequest.enrollmentId must not be null")
    @Positive(message = "CreatePaymentRequest.enrollmentId must be greater than 0")
    private Long enrollmentId;
}