package me.ifmo.backend.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.access.CourseAccessDTO;
import me.ifmo.backend.entities.CourseAccess;
import me.ifmo.backend.entities.OutboxMessage;
import me.ifmo.backend.entities.enums.AccessStatus;
import me.ifmo.backend.entities.enums.OutboxStatus;
import me.ifmo.backend.mappers.CourseAccessMapper;
import me.ifmo.backend.mappers.id.CourseIdMapper;
import me.ifmo.backend.mappers.id.UserIdMapper;
import me.ifmo.backend.repositories.CourseAccessRepository;
import me.ifmo.backend.services.CourseAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CourseAccessServiceImpl implements CourseAccessService {

    private static final String EVT_GRANT = "COURSE_ACCESS_GRANT_REQUESTED";
    private static final String EVT_REVOKE = "COURSE_ACCESS_REVOKE_REQUESTED";

    private final CourseAccessRepository courseAccessRepository;

    private final CourseAccessMapper courseAccessMapper;
    private final UserIdMapper userIdMapper;
    private final CourseIdMapper courseIdMapper;

    @Override
    @Transactional(readOnly = true)
    public CourseAccessDTO getAccess(Long userId, Long courseId) {
        CourseAccess access = courseAccessRepository.findByUser_IdAndCourse_Id(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "CourseAccess not found for userId=" + userId + ", courseId=" + courseId
                ));
        return courseAccessMapper.toDto(access);
    }

    @Override
    @Transactional
    public CourseAccessDTO grantAccess(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and courseId must be provided");
        }

        CourseAccess access = courseAccessRepository.findByUserAndCourseForUpdate(userId, courseId)
                .orElse(null);

        if (access == null) {
            CourseAccess created = new CourseAccess();
            created.setUser(userIdMapper.fromId(userId));
            created.setCourse(courseIdMapper.fromId(courseId));
            created.setStatus(AccessStatus.ACTIVE);
            created = courseAccessRepository.save(created);
            return courseAccessMapper.toDto(created);
        }

        if (access.getStatus() == AccessStatus.ACTIVE) {
            return courseAccessMapper.toDto(access);
        }

        access.setStatus(AccessStatus.ACTIVE);
        access.setRevokedAt(null);
        return courseAccessMapper.toDto(access);
    }

    @Override
    @Transactional
    public CourseAccessDTO revokeAccess(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and courseId must be provided");
        }

        CourseAccess access = courseAccessRepository.findByUserAndCourseForUpdate(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "CourseAccess not found for userId=" + userId + ", courseId=" + courseId
                ));

        if (access.getStatus() == AccessStatus.REVOKED) {
            return courseAccessMapper.toDto(access);
        }

        access.setStatus(AccessStatus.REVOKED);
        access.setRevokedAt(OffsetDateTime.now());
        return courseAccessMapper.toDto(access);
    }

    @Override
    @Transactional
    public void processAccessOutbox(OutboxMessage message) {
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OutboxMessage must be provided");
        }

        String eventType = message.getEventType();
        JsonNode payload = message.getPayload();

        Long userId = readLong(payload, "userId");
        Long courseId = readLong(payload, "courseId");

        try {
            if (EVT_GRANT.equals(eventType)) {
                grantAccess(userId, courseId);
            } else if (EVT_REVOKE.equals(eventType)) {
                revokeAccess(userId, courseId);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported outbox eventType: " + eventType);
            }

            message.setStatus(OutboxStatus.SENT);
            message.setLastError(null);

        } catch (RuntimeException ex) {
            message.setStatus(OutboxStatus.ERROR);
            message.setAttempts(message.getAttempts() == null ? 1 : message.getAttempts() + 1);
            message.setLastError(ex.getMessage());
            throw ex;
        }
    }

    private Long readLong(JsonNode payload, String field) {
        if (payload == null || payload.get(field) == null || payload.get(field).isNull()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outbox payload missing field: " + field);
        }
        JsonNode node = payload.get(field);
        if (!node.canConvertToLong()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outbox payload field is not a number: " + field);
        }
        return node.asLong();
    }
}