package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetTypeConverter;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для ответа с деталями расписания уведомлений")
public class NotificationScheduleResponse{ // Переименовал для консистентности

    @Schema(description = "Уникальный идентификатор расписания", example = "1")
    private Long id;

    @Schema(description = "Название расписания", example = "Ежедневное утреннее напоминание")
    private String name;

    @Schema(description = "Детали связанного шаблона уведомления")
    private NotificationTemplateDto notificationTemplate; // Используем NotificationTemplateDto

    @Schema(description = "ID пользователя, создавшего расписание", example = "101", nullable = true)
    private Long createdByUserId;

    @Schema(description = "Тип цели уведомления")
    private NotificationTargetType targetType;

    @Schema(description = "ID целевого пользователя (если targetType = SINGLE_USER)", example = "202", nullable = true)
    private Long targetUserId;

    @Schema(description = "CRON-выражение расписания", example = "0 0 9 * * MON-FRI")
    private String cronExpression;

    @Schema(description = "Дата и время начала действия расписания", example = "2024-06-01T09:00:00+03:00", nullable = true)
    private OffsetDateTime startDatetime;

    @Schema(description = "Дата и время окончания действия расписания", example = "2025-06-01T09:00:00+03:00", nullable = true)
    private OffsetDateTime endDatetime;

    @Schema(description = "Часовой пояс для CRON-выражения", example = "Europe/Moscow")
    private String timezone;

    @Schema(description = "Флаг активности расписания", example = "true")
    private boolean isActive;

    @Schema(description = "Целевые каналы для этого расписания (переопределяют настройки пользователя/шаблона)", example = "[\"EMAIL\"]", nullable = true)
    private List<String> targetChannels;

    @Schema(description = "Дата и время создания записи расписания", example = "2024-05-20T14:30:00+03:00")
    private OffsetDateTime createdAt;

    @Schema(description = "Дата и время последнего обновления записи расписания", example = "2024-05-21T10:00:00+03:00")
    private OffsetDateTime updatedAt;

    @Schema(description = "Время последнего фактического запуска по этому расписанию (если используется @Scheduled подход)", example = "2024-05-23T09:00:00+03:00", nullable = true)
    private OffsetDateTime lastTriggeredAtByScheduler; // Поле для отслеживания последнего запуска из @Scheduled
}