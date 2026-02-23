package me.ifmo.backend.DTO;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.ifmo.backend.entities.enums.PaymentStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;

    @NotNull(message = "Payment.enrollmentId must not be null")
    @Positive(message = "Payment.enrollmentId must be greater than 0")
    private Long enrollmentId;

    @NotNull(message = "Payment.amountCents must not be null")
    @Min(value = 0, message = "Payment.amountCents must be >= 0")
    private Integer amountCents;

    @NotBlank(message = "Payment.currency must not be blank")
    @Size(min = 3, max = 3, message = "Payment.currency must be exactly 3 characters")
    private String currency;

    @NotNull(message = "Payment.status must not be null")
    private PaymentStatus status;

    @NotBlank(message = "Payment.provider must not be blank")
    @Size(max = 64, message = "Payment.provider must be at most 64 characters")
    private String provider;

    @Size(max = 128, message = "Payment.providerPaymentId must be at most 128 characters")
    private String providerPaymentId;

    private String paymentLink;

    private OffsetDateTime expiresAt;
    private OffsetDateTime paidAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @AssertTrue(message = "Payment.paidAt can be set only when status is PAID or REFUNDED")
    public boolean isPaidAtConsistent() {
        return paidAt == null || status == PaymentStatus.PAID || status == PaymentStatus.REFUNDED;
    }
}