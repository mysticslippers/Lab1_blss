package me.ifmo.backend.DTO.common;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private OffsetDateTime timestamp;

    private String message;

    private String code;

    private String path;

    private List<FieldError> errors;

    public static ErrorResponse simple(String message) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .message(message)
                .build();
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}