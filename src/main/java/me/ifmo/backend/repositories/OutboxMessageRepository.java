package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.OutboxMessage;
import me.ifmo.backend.entities.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {

    List<OutboxMessage> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    @Query(value = """
            SELECT *
            FROM outbox_messages
            WHERE status = 'NEW'
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT :limit
            """, nativeQuery = true)
    List<OutboxMessage> pickBatchForSend(@Param("limit") int limit);
}