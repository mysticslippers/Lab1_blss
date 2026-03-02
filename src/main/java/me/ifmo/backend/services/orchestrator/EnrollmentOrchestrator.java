package me.ifmo.backend.services.orchestrator;

import me.ifmo.backend.client.PaymentProviderClient;
import me.ifmo.backend.DTO.requests.CreatePaymentRequestDTO;
import me.ifmo.backend.DTO.requests.EnrollRequestDTO;
import me.ifmo.backend.DTO.responses.CreatePaymentResponseDTO;
import me.ifmo.backend.DTO.responses.EnrollResponseDTO;
import me.ifmo.backend.entities.Course;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.mappers.EnrollmentMapper;
import me.ifmo.backend.services.CourseService;
import me.ifmo.backend.services.EnrollmentService;
import me.ifmo.backend.services.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentOrchestrator {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final PaymentService paymentService;
    private final PaymentProviderClient paymentProviderClient;
    private final EnrollmentMapper enrollmentMapper;

    private final String webhookCallbackUrl;

    public EnrollmentOrchestrator(
            CourseService courseService,
            EnrollmentService enrollmentService,
            PaymentService paymentService,
            PaymentProviderClient paymentProviderClient,
            EnrollmentMapper enrollmentMapper,
            @Value("${bank.callback-url}") String webhookCallbackUrl
    ) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.paymentService = paymentService;
        this.paymentProviderClient = paymentProviderClient;
        this.enrollmentMapper = enrollmentMapper;
        this.webhookCallbackUrl = webhookCallbackUrl;
    }

    @Transactional
    public EnrollResponseDTO enroll(EnrollRequestDTO request) {
        Course course = courseService.getActiveCourseOrThrow(request.courseId());

        enrollmentService.ensureNoOpenEnrollment(request.userId(), request.courseId());

        Enrollment enrollment = enrollmentService.createPending(request.userId(), course);

        CreatePaymentRequestDTO bankReq = new CreatePaymentRequestDTO(
                enrollment.getId().toString(),
                course.getPriceCents(),
                course.getCurrency(),
                webhookCallbackUrl
        );

        CreatePaymentResponseDTO bankResp = paymentProviderClient.createPayment(bankReq);

        paymentService.createNewForEnrollment(
                enrollment,
                bankResp.providerPaymentId(),
                course.getPriceCents(),
                course.getCurrency()
        );

        return enrollmentMapper.toEnrollResponseDto(enrollment, bankResp.paymentUrl());
    }
}