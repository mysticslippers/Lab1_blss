package me.ifmo.backend.DTO.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ifmo.backend.DTO.payment.PaymentInitResponse;
import me.ifmo.backend.entities.enums.EnrollmentStatus;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEnrollmentResponse {

    @NotNull(message = "CreateEnrollmentResponse.enrollmentId must not be null")
    private Long enrollmentId;

    @NotNull(message = "CreateEnrollmentResponse.status must not be null")
    private EnrollmentStatus status;

    @NotNull(message = "CreateEnrollmentResponse.payment must not be null")
    private PaymentInitResponse payment;
}