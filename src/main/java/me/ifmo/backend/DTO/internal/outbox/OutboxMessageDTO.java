package me.ifmo.backend.DTO.internal.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.ifmo.backend.entities.enums.OutboxStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxMessageDTO {

    private Long id;

    @NotBlank(message = "OutboxMessage.aggregateType must not be blank")
    @Size(max = 64, message = "OutboxMessage.aggregateType must be at most 64 characters")
    private String aggregateType;

    @NotNull(message = "OutboxMessage.aggregateId must not be null")
    @Positive(message = "OutboxMessage.aggregateId must be greater than 0")
    private Long aggregateId;

    @NotBlank(message = "OutboxMessage.eventType must not be blank")
    @Size(max = 64, message = "OutboxMessage.eventType must be at most 64 characters")
    private String eventType;

    @NotNull(message = "OutboxMessage.payload must not be null")
    private JsonNode payload;

    @NotNull(message = "OutboxMessage.status must not be null")
    private OutboxStatus status;

    @NotNull(message = "OutboxMessage.attempts must not be null")
    @Min(value = 0, message = "OutboxMessage.attempts must be >= 0")
    private Integer attempts;

    private String lastError;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}