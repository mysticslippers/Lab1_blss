package me.ifmo.backend.DTO.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ifmo.backend.entities.enums.AccessStatus;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import me.ifmo.backend.entities.enums.PaymentStatus;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentStatusResponse {

    @NotNull(message = "EnrollmentStatusResponse.enrollmentId must not be null")
    private Long enrollmentId;

    @NotNull(message = "EnrollmentStatusResponse.enrollmentStatus must not be null")
    private EnrollmentStatus enrollmentStatus;

    @NotNull(message = "EnrollmentStatusResponse.paymentStatus must not be null")
    private PaymentStatus paymentStatus;

    private AccessStatus accessStatus;
}