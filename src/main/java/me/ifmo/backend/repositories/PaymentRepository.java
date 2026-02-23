package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    Optional<Payment> findByEnrollment_IdAndStatusIn(Long enrollmentId, Collection<PaymentStatus> statuses);

    Optional<Payment> findTopByEnrollment_IdOrderByCreatedAtDesc(Long enrollmentId);

    List<Payment> findByEnrollment_IdOrderByCreatedAtDesc(Long enrollmentId);

    boolean existsByEnrollment_IdAndStatusIn(Long enrollmentId, Collection<PaymentStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT payment FROM Payment payment WHERE payment.id = :id")
    Optional<Payment> findByIdForUpdate(@Param("id") Long id);
}