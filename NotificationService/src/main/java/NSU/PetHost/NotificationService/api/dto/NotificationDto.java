package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.EventCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для представления уведомления")
public class NotificationDto {

    @Schema(description = "Уникальный идентификатор уведомления", example = "123")
    private Long id;

    @Schema(description = "ID пользователя-получателя", example = "456", required = true)
    private Long personId;

    @Schema(description = "Дата и время создания уведомления",
            type = "string", format = "date-time",
            example = "2024-01-15T14:30:00Z")
    private OffsetDateTime createdAt;

    @Schema(description = "Заголовок уведомления", example = "Важное обновление", maxLength = 255)
    private String title;

    @Schema(description = "Текст уведомления", example = "Не забудьте подтвердить email", required = true)
    private String message;

    @Schema(description = "Канал отправки",
            allowableValues = {"EMAIL", "SMS", "PUSH", "WEB"},
            example = "EMAIL",
            required = true)
    private String channel;

    @Schema(description = "ID связанного шаблона уведомления", example = "789")
    private Long notificationTemplateId;

    @Schema(description = "ID расписания отправки", example = "101112")
    private Long notificationScheduleId;

    @Schema(description = "Флаг прочтения уведомления", example = "false")
    private boolean isRead;
}