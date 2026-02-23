package me.ifmo.backend.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.ifmo.backend.entities.enums.RefundStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundDTO {

    private Long id;

    @NotNull(message = "Refund.paymentId must not be null")
    @Positive(message = "Refund.paymentId must be greater than 0")
    private Long paymentId;

    @NotNull(message = "Refund.amountCents must not be null")
    @Positive(message = "Refund.amountCents must be greater than 0")
    private Integer amountCents;

    @NotNull(message = "Refund.status must not be null")
    private RefundStatus status;

    @Size(max = 500, message = "Refund.reason must be at most 500 characters")
    private String reason;

    @Size(max = 128, message = "Refund.providerRefundId must be at most 128 characters")
    private String providerRefundId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime completedAt;
}