package ru.hogwarts.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Error response")
public class ErrorResponse {
    @Schema(description = "Error message", example = "Invalid file type")
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
