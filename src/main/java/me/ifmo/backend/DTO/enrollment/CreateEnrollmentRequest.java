package me.ifmo.backend.DTO.enrollment;

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
public class CreateEnrollmentRequest {

    @NotNull(message = "CreateEnrollmentRequest.courseId must not be null")
    @Positive(message = "CreateEnrollmentRequest.courseId must be greater than 0")
    private Long courseId;
}