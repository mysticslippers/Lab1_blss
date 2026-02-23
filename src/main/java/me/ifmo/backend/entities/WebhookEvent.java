package me.ifmo.backend.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ifmo.backend.entities.enums.WebhookProcessStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "webhook_events")
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false, length = 64)
    private String provider;

    @Column(name = "event_id", length = 128)
    private String eventId;

    @Column(name = "provider_payment_id", nullable = false, length = 128)
    private String providerPaymentId;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "process_status", nullable = false, columnDefinition = "webhook_process_status")
    private WebhookProcessStatus processStatus = WebhookProcessStatus.RECEIVED;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "received_at", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Override
    public String toString() {
        return "WebhookEvent{" +
                "id=" + id +
                ", provider='" + provider + '\'' +
                ", eventId='" + eventId + '\'' +
                ", providerPaymentId='" + providerPaymentId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", processStatus=" + processStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebhookEvent that = (WebhookEvent) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}