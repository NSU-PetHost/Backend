package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetTypeConverter;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank; // из jakarta.validation
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания или обновления расписания уведомлений")
public class NotificationScheduleRequest { // Переименовал для консистентности
    @Schema(description = "Название расписания", example = "Ежедневное утреннее напоминание", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Schema(description = "ID шаблона уведомления, который будет использоваться", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Notification template ID cannot be null")
    private Long notificationTemplateId;

    @Schema(description = "ID пользователя, создавшего расписание (если применимо, может быть null)", example = "101", nullable = true)
    private Long createdByUserId;

    @Schema(description = "Тип цели уведомления (SINGLE_USER или ALL_USERS)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Target type cannot be null")
    private NotificationTargetType targetType;

    @Schema(description = "ID целевого пользователя (обязательно, если targetType = SINGLE_USER)", example = "202", nullable = true)
    private Long targetUserId;

    @Schema(description = "CRON-выражение для определения расписания (например, '0 0/30 * * * ?' для каждых 30 минут)",
            example = "0 0 9 * * MON-FRI", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Cron expression cannot be blank")
    // Здесь можно добавить @Pattern для более строгой валидации формата CRON, если нужно
    private String cronExpression;

    @Schema(description = "Дата и время начала действия расписания (формат ISO OffsetDateTime, например, 2024-06-01T09:00:00+07:00). Если не указано, начнет действовать немедленно.",
            example = "2024-06-01T09:00:00+03:00", nullable = true)
    private OffsetDateTime startDatetime;

    @Schema(description = "Дата и время окончания действия расписания (формат ISO OffsetDateTime). Если не указано, будет действовать бессрочно.",
            example = "2025-06-01T09:00:00+03:00", nullable = true)
    private OffsetDateTime endDatetime;

    @Schema(description = "Часовой пояс для CRON-выражения (например, 'UTC', 'Europe/Moscow')", example = "Europe/Moscow", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Timezone cannot be blank")
    private String timezone;

    @Schema(description = "Флаг активности расписания. Если false, расписание не будет запускаться.", example = "true", defaultValue = "true")
    private Boolean isActive = true; // Используем Boolean, чтобы null означал "не менять при обновлении"

    @Schema(description = "Список каналов, по которым будет отправлено уведомление (например, EMAIL, ON_SITE). Если не указано, используются каналы из настроек пользователя или шаблона.",
            example = "[\"EMAIL\", \"PUSH\"]", nullable = true)
    private List<String> targetChannels;
}