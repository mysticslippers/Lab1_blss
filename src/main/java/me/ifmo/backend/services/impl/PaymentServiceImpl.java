package me.ifmo.backend.services.impl;

import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.PaymentStatus;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.PaymentRepository;
import me.ifmo.backend.services.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment createNewForEnrollment(Enrollment enrollment, String providerPaymentId, int amountCents, String currency) {
        Payment payment = Payment.builder()
                .enrollment(enrollment)
                .providerPaymentId(providerPaymentId)
                .amountCents(amountCents)
                .currency(currency)
                .status(PaymentStatus.NEW)
                .retryCount((short) 0)
                .maxRetries((short) 3)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getByProviderPaymentIdOrThrow(String providerPaymentId) {
        return paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found by providerPaymentId=" + providerPaymentId));
    }

    @Override
    public Optional<Payment> findByEnrollmentId(Long enrollmentId) {
        return paymentRepository.findByEnrollment_Id(enrollmentId);
    }

    @Override
    @Transactional
    public Payment updateStatus(Payment payment, PaymentStatus newStatus) {
        payment.setStatus(newStatus);
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findExpiredCandidates(PaymentStatus status, LocalDateTime before) {
        return paymentRepository.findExpiredCandidates(status, before);
    }

    @Override
    @Transactional
    public int bulkUpdateStatus(List<Long> ids, PaymentStatus newStatus) {
        return paymentRepository.bulkUpdateStatus(ids, newStatus);
    }
}