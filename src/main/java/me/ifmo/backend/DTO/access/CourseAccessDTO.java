package me.ifmo.backend.DTO.access;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import me.ifmo.backend.entities.enums.AccessStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAccessDTO {

    private Long id;

    @NotNull(message = "CourseAccess.userId must not be null")
    @Positive(message = "CourseAccess.userId must be greater than 0")
    private Long userId;

    @NotNull(message = "CourseAccess.courseId must not be null")
    @Positive(message = "CourseAccess.courseId must be greater than 0")
    private Long courseId;

    @NotNull(message = "CourseAccess.status must not be null")
    private AccessStatus status;

    private OffsetDateTime grantedAt;
    private OffsetDateTime revokedAt;
    private OffsetDateTime updatedAt;
}