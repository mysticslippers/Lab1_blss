package me.ifmo.backend.mappers;

import me.ifmo.backend.DTO.internal.outbox.OutboxMessageDTO;
import me.ifmo.backend.entities.OutboxMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OutboxMessageMapper {

    OutboxMessageDTO toDto(OutboxMessage message);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OutboxMessage toEntity(OutboxMessageDTO dto);
}