package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.WebhookEvent;
import me.ifmo.backend.entities.enums.WebhookProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

    Optional<WebhookEvent> findByProviderAndEventId(String provider, String eventId);

    Optional<WebhookEvent> findByProviderAndProviderPaymentIdAndEventType(
            String provider,
            String providerPaymentId,
            String eventType
    );

    List<WebhookEvent> findTop100ByProcessStatusOrderByReceivedAtAsc(WebhookProcessStatus status);
}