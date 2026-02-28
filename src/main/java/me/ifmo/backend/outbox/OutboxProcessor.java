package me.ifmo.backend.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.entities.OutboxMessage;
import me.ifmo.backend.entities.enums.OutboxStatus;
import me.ifmo.backend.repositories.OutboxMessageRepository;
import me.ifmo.backend.services.CourseAccessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private static final String EVT_PAYMENT_INIT = "PAYMENT_INIT_REQUESTED";
    private static final String EVT_ACCESS_GRANT = "COURSE_ACCESS_GRANT_REQUESTED";
    private static final String EVT_ACCESS_REVOKE = "COURSE_ACCESS_REVOKE_REQUESTED";

    private static final int DEFAULT_BATCH_SIZE = 50;

    private final OutboxMessageRepository outboxMessageRepository;
    private final CourseAccessService courseAccessService;
    private final MinibankOutboxPublisher minibankOutboxPublisher;
    
    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:1000}")
    @Transactional
    public void poll() {
        processInternal();
    }
    
    @Transactional
    public ProcessResult processOnce() {
        return processInternal();
    }

    private ProcessResult processInternal() {
        List<OutboxMessage> batch = outboxMessageRepository.pickBatchForSend(DEFAULT_BATCH_SIZE);
        if (batch.isEmpty()) {
            return new ProcessResult(0, 0);
        }

        int processed = 0;
        int errors = 0;

        for (OutboxMessage msg : batch) {
            try {
                dispatch(msg);
                processed++;
            } catch (Exception exception) {
                markError(msg, exception);
                errors++;
                log.warn("Outbox message {} failed (attempts={}): {}",
                        msg.getId(),
                        msg.getAttempts(),
                        exception.getMessage());
            }
        }

        return new ProcessResult(processed, errors);
    }

    private void dispatch(OutboxMessage msg) {
        String eventType = msg.getEventType();

        if (EVT_PAYMENT_INIT.equals(eventType)) {
            minibankOutboxPublisher.publishPaymentInit(msg);
            markSent(msg);
            return;
        }

        if (EVT_ACCESS_GRANT.equals(eventType) || EVT_ACCESS_REVOKE.equals(eventType)) {
            courseAccessService.processAccessOutbox(msg);
            return;
        }

        throw new IllegalArgumentException("Unsupported outbox eventType: " + eventType);
    }

    private void markSent(OutboxMessage msg) {
        msg.setStatus(OutboxStatus.SENT);
        msg.setLastError(null);
    }

    private void markError(OutboxMessage msg, Exception exception) {
        msg.setStatus(OutboxStatus.ERROR);
        msg.setAttempts(msg.getAttempts() == null ? 1 : msg.getAttempts() + 1);
        msg.setLastError(exception.getMessage());
    }

    public record ProcessResult(int processed, int errors) {}
}