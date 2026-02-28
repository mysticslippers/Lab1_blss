package me.ifmo.backend.services;

import me.ifmo.backend.DTO.access.CourseAccessDTO;
import me.ifmo.backend.entities.OutboxMessage;

public interface CourseAccessService {

    CourseAccessDTO getAccess(Long userId, Long courseId);

    CourseAccessDTO grantAccess(Long userId, Long courseId);

    CourseAccessDTO revokeAccess(Long userId, Long courseId);

    void processAccessOutbox(OutboxMessage message);
}