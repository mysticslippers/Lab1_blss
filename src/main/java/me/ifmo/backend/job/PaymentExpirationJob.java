package me.ifmo.backend.job;

import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.EnrollmentStatus;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.PaymentStatus;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.services.PaymentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentExpirationJob {

    private static final int EXPIRE_AFTER_HOURS = 24;

    private final PaymentService paymentService;
    private final EnrollmentRepository enrollmentRepository;

    public PaymentExpirationJob(PaymentService paymentService,
                                EnrollmentRepository enrollmentRepository) {
        this.paymentService = paymentService;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Scheduled(fixedDelayString = "${jobs.payment-expiration.fixed-delay-ms:60000}")
    @Transactional
    public void expirePayments() {
        LocalDateTime before = LocalDateTime.now().minusHours(EXPIRE_AFTER_HOURS);

        List<Payment> candidates = paymentService.findExpiredCandidates(PaymentStatus.NEW, before);
        if (candidates.isEmpty()) {
            return;
        }
        
        List<Long> ids = candidates.stream().map(Payment::getId).toList();
        paymentService.bulkUpdateStatus(ids, PaymentStatus.EXPIRED);
        
        List<Enrollment> enrollmentsToReject = candidates.stream()
                .map(Payment::getEnrollment)
                .filter(enrollment -> enrollment != null && enrollment.getStatus() == EnrollmentStatus.PENDING_PAYMENT)
                .peek(enrollment -> {
                    enrollment.setStatus(EnrollmentStatus.REJECTED);
                    enrollment.setRejectReason("PAYMENT_EXPIRED");
                })
                .toList();

        if (!enrollmentsToReject.isEmpty()) {
            enrollmentRepository.saveAll(enrollmentsToReject);
        }
    }
}