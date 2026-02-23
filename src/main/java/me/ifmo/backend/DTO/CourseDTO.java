package me.ifmo.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Long id;

    @NotBlank(message = "Course.title must not be blank")
    private String title;

    private String description;

    @NotNull(message = "Course.priceCents must not be null")
    private Integer priceCents;

    @NotBlank(message = "Course.currency must not be blank")
    private String currency;

    private Integer capacity;

    private String status;

    @NotNull(message = "Course.enrollmentOpen must not be null")
    private Boolean enrollmentOpen;

    private OffsetDateTime startsAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}