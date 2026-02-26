package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.internal.webhook.WebhookEventDTO;
import me.ifmo.backend.entities.WebhookEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebhookEventMapper {

    WebhookEventDTO toDto(WebhookEvent event);

    @Mapping(target = "provider", constant = "MINIBANK")
    @Mapping(target = "receivedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WebhookEvent toEntity(WebhookEventDTO dto);
}