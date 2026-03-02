package me.ifmo.backend.services.impl;

import me.ifmo.backend.DTO.webhook.PaymentWebhookDTO;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.EnrollmentStatus;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.PaymentStatus;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.PaymentRepository;
import me.ifmo.backend.services.PaymentWebhookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWebhookServiceImpl implements PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public PaymentWebhookServiceImpl(PaymentRepository paymentRepository,
                                     EnrollmentRepository enrollmentRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional
    public void handlePaymentWebhook(PaymentWebhookDTO dto) {
        Payment payment = resolvePayment(dto);

        PaymentStatus newStatus = parseStatus(dto.status());

        payment.setStatus(newStatus);
        paymentRepository.save(payment);

        Enrollment enrollment = payment.getEnrollment();
        if (enrollment == null) {
            throw new NotFoundException("Enrollment for payment (providerPaymentId=" + dto.providerPaymentId() + ") not found");
        }

        if (newStatus == PaymentStatus.PAID) {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.setRejectReason(null);
            enrollmentRepository.save(enrollment);
        } else if (newStatus == PaymentStatus.FAILED) {
            enrollment.setStatus(EnrollmentStatus.REJECTED);
            enrollment.setRejectReason("PAYMENT_FAILED");
            enrollmentRepository.save(enrollment);
        }
    }

    private Payment resolvePayment(PaymentWebhookDTO dto) {
        if (dto.providerPaymentId() != null && !dto.providerPaymentId().isBlank()) {
            return paymentRepository.findByProviderPaymentId(dto.providerPaymentId())
                    .orElseThrow(() -> new NotFoundException(
                            "Payment with providerPaymentId " + dto.providerPaymentId() + " not found"
                    ));
        }

        if (dto.merchantPaymentRef() != null && !dto.merchantPaymentRef().isBlank()) {
            Long ref;
            try {
                ref = Long.parseLong(dto.merchantPaymentRef());
            } catch (NumberFormatException ex) {
                throw new NotFoundException("merchantPaymentRef is not numeric: " + dto.merchantPaymentRef());
            }

            return paymentRepository.findByEnrollment_Id(ref)
                    .or(() -> paymentRepository.findById(ref))
                    .orElseThrow(() -> new NotFoundException(
                            "Payment not found by merchantPaymentRef=" + dto.merchantPaymentRef()
                    ));
        }

        throw new NotFoundException("Webhook does not contain providerPaymentId or merchantPaymentRef");
    }

    private PaymentStatus parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new IllegalArgumentException("Webhook status is blank");
        }
        return PaymentStatus.valueOf(rawStatus.trim().toUpperCase());
    }
}