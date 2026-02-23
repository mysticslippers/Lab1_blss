package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Refund;
import me.ifmo.backend.entities.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByProviderRefundId(String providerRefundId);

    List<Refund> findByPayment_IdOrderByCreatedAtDesc(Long paymentId);

    Optional<Refund> findTopByPayment_IdOrderByCreatedAtDesc(Long paymentId);

    List<Refund> findByPayment_IdAndStatusIn(Long paymentId, Collection<RefundStatus> statuses);
}