package me.ifmo.backend.DTO.webhook;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookAckResponse {

    @NotNull(message = "WebhookAckResponse.accepted must not be null")
    private Boolean accepted;

    private String message;

    public static WebhookAckResponse ok() {
        return WebhookAckResponse.builder()
                .accepted(true)
                .message("accepted")
                .build();
    }

    public static WebhookAckResponse rejected(String message) {
        return WebhookAckResponse.builder()
                .accepted(false)
                .message(message)
                .build();
    }
}