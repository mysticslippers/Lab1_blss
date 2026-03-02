package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.responses.EnrollResponseDTO;
import me.ifmo.backend.DTO.responses.EnrollmentInfoResponseDTO;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.entities.EnrollmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "enrollmentId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "paymentUrl", expression = "java(paymentUrl)")
    @Mapping(target = "message", expression = "java(buildMessage(enrollment))")
    EnrollResponseDTO toEnrollResponseDto(Enrollment enrollment, String paymentUrl);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "rejectReason", source = "rejectReason")
    EnrollmentInfoResponseDTO toEnrollmentInfoResponseDto(Enrollment enrollment);

    default String buildMessage(Enrollment enrollment) {
        EnrollmentStatus status = enrollment.getStatus();
        if (status == null) return "Unknown";

        return switch (status) {
            case PENDING_PAYMENT -> "Payment required";
            case ACTIVE -> "Enrollment active";
            case REJECTED -> {
                String reason = enrollment.getRejectReason();
                yield (reason != null && !reason.isBlank()) ? reason : "Rejected";
            }
        };
    }
}