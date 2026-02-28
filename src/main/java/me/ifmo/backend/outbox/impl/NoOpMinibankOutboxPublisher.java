package me.ifmo.backend.outbox.impl;

import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.entities.OutboxMessage;
import me.ifmo.backend.outbox.MinibankOutboxPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpMinibankOutboxPublisher implements MinibankOutboxPublisher {

    @Override
    public void publishPaymentInit(OutboxMessage message) {
        log.info("NO-OP publish to MINIBANK: {}", message);
    }
}