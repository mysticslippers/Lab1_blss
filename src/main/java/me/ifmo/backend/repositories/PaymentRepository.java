package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    Optional<Payment> findByEnrollment_Id(Long enrollmentId);

    @Query("""
        select p from Payment p
        where p.status = :status and p.createdAt < :before
    """)
    List<Payment> findExpiredCandidates(PaymentStatus status, LocalDateTime before);

    @Modifying
    @Query("""
        update Payment p
        set p.status = :newStatus
        where p.id in :ids
    """)
    int bulkUpdateStatus(List<Long> ids, PaymentStatus newStatus);
}
