package me.ifmo.backend.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.payment.CreatePaymentRequest;
import me.ifmo.backend.DTO.payment.PaymentInitResponse;
import me.ifmo.backend.DTO.webhook.PaymentWebhookRequest;
import me.ifmo.backend.DTO.webhook.WebhookAckResponse;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.OutboxMessage;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.WebhookEvent;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import me.ifmo.backend.entities.enums.OutboxStatus;
import me.ifmo.backend.entities.enums.PaymentStatus;
import me.ifmo.backend.entities.enums.WebhookProcessStatus;
import me.ifmo.backend.exceptions.AlreadyProcessedException;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.OutboxMessageRepository;
import me.ifmo.backend.repositories.PaymentRepository;
import me.ifmo.backend.repositories.WebhookEventRepository;
import me.ifmo.backend.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String PROVIDER_MINIBANK = "MINIBANK";

    private static final EnumSet<PaymentStatus> ACTIVE_PAYMENT_STATUSES = EnumSet.of(
            PaymentStatus.CREATED,
            PaymentStatus.PENDING
    );

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final WebhookEventRepository webhookEventRepository;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentInitResponse createPayment(CreatePaymentRequest request) {
        Long enrollmentId = request.getEnrollmentId();

        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found: " + enrollmentId));

        Payment activePayment = paymentRepository
                .findByEnrollment_IdAndStatusIn(enrollmentId, ACTIVE_PAYMENT_STATUSES)
                .orElse(null);

        if (activePayment != null) {
            return toInitResponse(activePayment);
        }

        Course course = enrollment.getCourse();
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment has no course");
        }

        Payment payment = new Payment();
        payment.setEnrollment(enrollment);
        payment.setAmountCents(course.getPriceCents());
        payment.setCurrency(course.getCurrency());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setExpiresAt(enrollment.getPaymentExpiresAt());

        payment = paymentRepository.save(payment);

        outboxMessageRepository.save(buildPaymentInitOutbox(payment, enrollment, course));

        if (enrollment.getStatus() == EnrollmentStatus.NEW) {
            enrollment.setStatus(EnrollmentStatus.PENDING_PAYMENT);
        }

        return toInitResponse(payment);
    }

    @Override
    @Transactional
    public PaymentInitResponse attachProviderDetails(Long paymentId, String providerPaymentId, String paymentLink) {
        if (paymentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentId must be provided");
        }
        if (providerPaymentId == null || providerPaymentId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "providerPaymentId must be provided");
        }

        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found: " + paymentId));

        if (payment.getProviderPaymentId() != null && payment.getProviderPaymentId().equals(providerPaymentId)) {
            return toInitResponse(payment);
        }

        if (isFinal(payment.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment already in final status: " + payment.getStatus()
            );
        }

        paymentRepository.findByProviderPaymentId(providerPaymentId).ifPresent(existing -> {
            if (!existing.getId().equals(payment.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "providerPaymentId already used");
            }
        });

        payment.setProviderPaymentId(providerPaymentId);
        payment.setPaymentLink(paymentLink);

        if (payment.getStatus() == PaymentStatus.CREATED) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        return toInitResponse(payment);
    }

    @Override
    @Transactional
    public WebhookAckResponse handleWebhook(PaymentWebhookRequest request) {
        webhookEventRepository.findByProviderAndEventId(PROVIDER_MINIBANK, request.getEventId())
                .ifPresent(exception -> { throw new AlreadyProcessedException(); });

        WebhookEvent event = new WebhookEvent();
        event.setProvider(PROVIDER_MINIBANK);
        event.setEventId(request.getEventId());
        event.setProviderPaymentId(request.getProviderPaymentId());
        event.setEventType(request.getEventType());
        event.setPayload(objectMapper.valueToTree(request));
        event.setProcessStatus(WebhookProcessStatus.RECEIVED);

        event = webhookEventRepository.save(event);

        try {
            Payment payment = paymentRepository.findByProviderPaymentId(request.getProviderPaymentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Payment not found by providerPaymentId: " + request.getProviderPaymentId()
                    ));

            Payment lockedPayment = paymentRepository.findByIdForUpdate(payment.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Payment not found: " + payment.getId()
                    ));

            Enrollment enrollment = enrollmentRepository.findByIdForUpdate(lockedPayment.getEnrollment().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

            applyPaymentStatusUpdate(lockedPayment, enrollment, request);

            event.setProcessStatus(WebhookProcessStatus.PROCESSED);
            event.setProcessedAt(OffsetDateTime.now());
            webhookEventRepository.save(event);

            return WebhookAckResponse.ok();

        } catch (AlreadyProcessedException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            event.setProcessStatus(WebhookProcessStatus.ERROR);
            event.setErrorMessage(exception.getMessage());
            webhookEventRepository.save(event);
            return WebhookAckResponse.rejected(exception.getMessage());
        }
    }

    private void applyPaymentStatusUpdate(Payment payment, Enrollment enrollment, PaymentWebhookRequest request) {
        PaymentStatus newStatus = request.getStatus();
        PaymentStatus oldStatus = payment.getStatus();

        if (isFinal(oldStatus) && oldStatus != newStatus) {
            return;
        }

        payment.setStatus(newStatus);

        if (newStatus == PaymentStatus.PAID) {
            payment.setPaidAt(request.getPaidAt() != null ? request.getPaidAt() : OffsetDateTime.now());
            enrollment.setStatus(EnrollmentStatus.ENROLLED);

            outboxMessageRepository.save(buildAccessGrantOutbox(enrollment));
            return;
        }

        if (newStatus == PaymentStatus.FAILED) {
            enrollment.setStatus(EnrollmentStatus.PAYMENT_FAILED);
            return;
        }

        if (newStatus == PaymentStatus.EXPIRED) {
            enrollment.setStatus(EnrollmentStatus.EXPIRED);
            return;
        }

        if (newStatus == PaymentStatus.REFUNDED) {
            enrollment.setStatus(EnrollmentStatus.REFUNDED);
            outboxMessageRepository.save(buildAccessRevokeOutbox(enrollment));
        }
    }

    private boolean isFinal(PaymentStatus status) {
        return status == PaymentStatus.PAID
                || status == PaymentStatus.FAILED
                || status == PaymentStatus.EXPIRED
                || status == PaymentStatus.REFUNDED;
    }

    private PaymentInitResponse toInitResponse(Payment payment) {
        return PaymentInitResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .paymentLink(payment.getPaymentLink())
                .expiresAt(payment.getExpiresAt())
                .providerPaymentId(payment.getProviderPaymentId())
                .build();
    }

    private OutboxMessage buildPaymentInitOutbox(Payment payment, Enrollment enrollment, Course course) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("paymentId", payment.getId());
        payload.put("enrollmentId", enrollment.getId());
        payload.put("courseId", course.getId());
        payload.put("userId", enrollment.getUser().getId());
        payload.put("amountCents", payment.getAmountCents());
        payload.put("currency", payment.getCurrency());
        if (payment.getExpiresAt() != null) payload.put("expiresAt", payment.getExpiresAt().toString());

        OutboxMessage msg = new OutboxMessage();
        msg.setAggregateType("PAYMENT");
        msg.setAggregateId(payment.getId());
        msg.setEventType("PAYMENT_INIT_REQUESTED");
        msg.setPayload(payload);
        msg.setStatus(OutboxStatus.NEW);
        return msg;
    }

    private OutboxMessage buildAccessGrantOutbox(Enrollment enrollment) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("enrollmentId", enrollment.getId());
        payload.put("userId", enrollment.getUser().getId());
        payload.put("courseId", enrollment.getCourse().getId());

        OutboxMessage msg = new OutboxMessage();
        msg.setAggregateType("ENROLLMENT");
        msg.setAggregateId(enrollment.getId());
        msg.setEventType("COURSE_ACCESS_GRANT_REQUESTED");
        msg.setPayload(payload);
        msg.setStatus(OutboxStatus.NEW);
        return msg;
    }

    private OutboxMessage buildAccessRevokeOutbox(Enrollment enrollment) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("enrollmentId", enrollment.getId());
        payload.put("userId", enrollment.getUser().getId());
        payload.put("courseId", enrollment.getCourse().getId());

        OutboxMessage msg = new OutboxMessage();
        msg.setAggregateType("ENROLLMENT");
        msg.setAggregateId(enrollment.getId());
        msg.setEventType("COURSE_ACCESS_REVOKE_REQUESTED");
        msg.setPayload(payload);
        msg.setStatus(OutboxStatus.NEW);
        return msg;
    }
}