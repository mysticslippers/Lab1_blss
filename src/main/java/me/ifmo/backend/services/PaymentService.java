package me.ifmo.backend.services;

import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment createNewForEnrollment(Enrollment enrollment, String providerPaymentId, int amountCents, String currency);

    Payment getByProviderPaymentIdOrThrow(String providerPaymentId);

    Optional<Payment> findByEnrollmentId(Long enrollmentId);

    Payment updateStatus(Payment payment, PaymentStatus newStatus);

    List<Payment> findExpiredCandidates(PaymentStatus status, LocalDateTime before);

    int bulkUpdateStatus(List<Long> ids, PaymentStatus newStatus);
}