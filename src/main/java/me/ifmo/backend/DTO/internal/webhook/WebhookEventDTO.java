package me.ifmo.backend.DTO.internal.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.ifmo.backend.entities.enums.WebhookProcessStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEventDTO {

    private Long id;

    @Size(max = 128, message = "WebhookEvent.eventId must be at most 128 characters")
    private String eventId;

    @NotBlank(message = "WebhookEvent.providerPaymentId must not be blank")
    @Size(max = 128, message = "WebhookEvent.providerPaymentId must be at most 128 characters")
    private String providerPaymentId;

    @NotBlank(message = "WebhookEvent.eventType must not be blank")
    @Size(max = 64, message = "WebhookEvent.eventType must be at most 64 characters")
    private String eventType;

    @NotNull(message = "WebhookEvent.payload must not be null")
    private JsonNode payload;

    @NotNull(message = "WebhookEvent.processStatus must not be null")
    private WebhookProcessStatus processStatus;

    private String errorMessage;

    private OffsetDateTime receivedAt;
    private OffsetDateTime processedAt;
    private OffsetDateTime updatedAt;
}