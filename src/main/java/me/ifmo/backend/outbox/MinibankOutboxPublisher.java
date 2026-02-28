package me.ifmo.backend.outbox;

import me.ifmo.backend.entities.OutboxMessage;

public interface MinibankOutboxPublisher {
    void publishPaymentInit(OutboxMessage message);
}