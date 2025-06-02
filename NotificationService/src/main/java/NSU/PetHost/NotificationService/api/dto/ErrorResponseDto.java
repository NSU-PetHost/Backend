package NSU.PetHost.NotificationService.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema; // Импорт аннотации @Schema
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Стандартизированный ответ об ошибке API")
public class ErrorResponseDto {

    @Schema(description = "Дата и время возникновения ошибки", example = "2024-05-23T10:15:30.123")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус код ошибки", example = "400")
    private int status;

    @Schema(description = "Краткое описание HTTP статуса", example = "Bad Request")
    private String error;

    @Schema(description = "Человекочитаемое сообщение об ошибке", example = "Validation Failed")
    private String message;

    @Schema(description = "URI запроса, который привел к ошибке", example = "/api/v1/schedules")
    private String path;

    @Schema(description = "Опциональный список деталей ошибки (например, для ошибок валидации полей)",
            example = "[\"name: Name cannot be blank\", \"cronExpression: Cron expression is required\"]",
            nullable = true)
    private List<String> details;
}