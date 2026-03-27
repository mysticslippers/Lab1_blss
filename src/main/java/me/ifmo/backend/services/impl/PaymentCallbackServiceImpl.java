package me.ifmo.backend.services.impl;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import me.ifmo.backend.entities.enums.PaymentStatus;
import me.ifmo.backend.exceptions.BusinessException;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.PaymentRepository;
import me.ifmo.backend.services.PaymentCallbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCallbackServiceImpl implements PaymentCallbackService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void markPaymentAsPaid(String providerPaymentId) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new NotFoundException(
                        "Payment with provider payment id " + providerPaymentId + " not found"
                ));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.FAILED || payment.getStatus() == PaymentStatus.EXPIRED) {
            throw new BusinessException(
                    "Payment with provider payment id " + providerPaymentId + " cannot be marked as PAID"
            );
        }

        Enrollment enrollment = payment.getEnrollment();
        Course course = enrollment.getCourse();

        if (course.getAvailablePlaces() == null || course.getAvailablePlaces() <= 0) {
            throw new BusinessException(
                    "No available places left for course with id " + course.getId()
            );
        }

        LocalDateTime now = LocalDateTime.now();

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(now);
        payment.setUpdatedAt(now);

        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setActivatedAt(now);
        enrollment.setUpdatedAt(now);

        course.setAvailablePlaces(course.getAvailablePlaces() - 1);
        course.setUpdatedAt(now);

        enrollmentRepository.save(enrollment);
        paymentRepository.save(payment);
    }

    @Override
    public void markPaymentAsFailed(String providerPaymentId, String failureReason) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new NotFoundException(
                        "Payment with provider payment id " + providerPaymentId + " not found"
                ));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new BusinessException(
                    "Payment with provider payment id " + providerPaymentId + " is already PAID"
            );
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(failureReason);
        payment.setUpdatedAt(now);

        Enrollment enrollment = payment.getEnrollment();
        enrollment.setStatus(EnrollmentStatus.PAYMENT_FAILED);
        enrollment.setRejectionReason(failureReason);
        enrollment.setUpdatedAt(now);

        enrollmentRepository.save(enrollment);
        paymentRepository.save(payment);
    }

    @Override
    public void markExpiredPayments() {
        List<Enrollment> expiredEnrollments =
                enrollmentRepository.findAllByStatusAndPaymentExpiresAtBefore(
                        EnrollmentStatus.PENDING_PAYMENT,
                        LocalDateTime.now()
                );

        LocalDateTime now = LocalDateTime.now();

        for (Enrollment enrollment : expiredEnrollments) {
            enrollment.setStatus(EnrollmentStatus.PAYMENT_EXPIRED);
            enrollment.setUpdatedAt(now);

            Payment payment = paymentRepository.findByEnrollmentId(enrollment.getId()).orElse(null);
            if (payment != null && payment.getStatus() != PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.EXPIRED);
                payment.setUpdatedAt(now);
                paymentRepository.save(payment);
            }

            enrollmentRepository.save(enrollment);
        }
    }
}