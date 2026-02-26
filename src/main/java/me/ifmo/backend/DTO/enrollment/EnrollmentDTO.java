package me.ifmo.backend.DTO.enrollment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.ifmo.backend.entities.enums.EnrollmentStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {

    private Long id;

    @NotNull(message = "Enrollment.userId must not be null")
    @Positive(message = "Enrollment.userId must be greater than 0")
    private Long userId;

    @NotNull(message = "Enrollment.courseId must not be null")
    @Positive(message = "Enrollment.courseId must be greater than 0")
    private Long courseId;

    @NotNull(message = "Enrollment.status must not be null")
    private EnrollmentStatus status;

    @Size(max = 500, message = "Enrollment.rejectionReason must be at most 500 characters")
    private String rejectionReason;

    private OffsetDateTime paymentExpiresAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}