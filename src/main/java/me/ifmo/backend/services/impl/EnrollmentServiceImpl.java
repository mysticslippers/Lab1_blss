package me.ifmo.backend.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentRequest;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentResponse;
import me.ifmo.backend.DTO.enrollment.EnrollmentStatusResponse;
import me.ifmo.backend.DTO.payment.PaymentInitResponse;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.OutboxMessage;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.entities.enums.CourseStatus;
import me.ifmo.backend.entities.enums.EnrollmentStatus;
import me.ifmo.backend.entities.enums.PaymentStatus;
import me.ifmo.backend.mappers.id.UserIdMapper;
import me.ifmo.backend.repositories.CourseRepository;
import me.ifmo.backend.repositories.EnrollmentRepository;
import me.ifmo.backend.repositories.OutboxMessageRepository;
import me.ifmo.backend.repositories.PaymentRepository;
import me.ifmo.backend.services.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Duration PAYMENT_TTL = Duration.ofHours(24);

    private static final EnumSet<EnrollmentStatus> ACTIVE_ENROLLMENT_STATUSES = EnumSet.of(
            EnrollmentStatus.NEW,
            EnrollmentStatus.PENDING_PAYMENT,
            EnrollmentStatus.ENROLLED
    );

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final OutboxMessageRepository outboxMessageRepository;

    private final UserIdMapper userIdMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CreateEnrollmentResponse createEnrollment(Long userId, CreateEnrollmentRequest request) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must be provided");
        }

        Long courseId = request.getCourseId();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));

        if (course.getStatus() != CourseStatus.PUBLISHED || Boolean.FALSE.equals(course.getEnrollmentOpen())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment is closed for course: " + courseId);
        }

        boolean alreadyExists = enrollmentRepository.existsByUser_IdAndCourse_IdAndStatusIn(
                userId,
                courseId,
                ACTIVE_ENROLLMENT_STATUSES
        );
        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Active enrollment already exists for this course");
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plus(PAYMENT_TTL);

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(userIdMapper.fromId(userId));
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDING_PAYMENT);
        enrollment.setPaymentExpiresAt(expiresAt);
        enrollment = enrollmentRepository.save(enrollment);

        Payment payment = new Payment();
        payment.setEnrollment(enrollment);
        payment.setAmountCents(course.getPriceCents());
        payment.setCurrency(course.getCurrency());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setExpiresAt(expiresAt);
        payment = paymentRepository.save(payment);

        outboxMessageRepository.save(buildPaymentInitOutbox(payment, enrollment, course));

        return CreateEnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .status(enrollment.getStatus())
                .payment(PaymentInitResponse.builder()
                        .paymentId(payment.getId())
                        .status(payment.getStatus())
                        .expiresAt(payment.getExpiresAt())
                        .paymentLink(payment.getPaymentLink())
                        .providerPaymentId(payment.getProviderPaymentId())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentStatusResponse getStatus(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found: " + enrollmentId));

        PaymentStatus paymentStatus = paymentRepository.findTopByEnrollment_IdOrderByCreatedAtDesc(enrollmentId)
                .map(Payment::getStatus)
                .orElse(null);

        return EnrollmentStatusResponse.builder()
                .enrollmentId(enrollment.getId())
                .enrollmentStatus(enrollment.getStatus())
                .paymentStatus(paymentStatus)
                .accessStatus(null)
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
        if (payment.getExpiresAt() != null) {
            payload.put("expiresAt", payment.getExpiresAt().toString());
        }

        OutboxMessage msg = new OutboxMessage();
        msg.setAggregateType("PAYMENT");
        msg.setAggregateId(payment.getId());
        msg.setEventType("PAYMENT_INIT_REQUESTED");
        msg.setPayload(payload);
        return msg;
    }
}