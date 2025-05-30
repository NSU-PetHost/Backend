package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTemplateType;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для шаблона уведомлений")
public class NotificationTemplateDto {
    @Schema(description = "Уникальный идентификатор шаблона", example = "1", accessMode = Schema.AccessMode.READ_ONLY) // Обычно ID только для чтения в ответах
    private Long id;

    @Schema(description = "Уникальное имя шаблона", example = "WELCOME_EMAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "Шаблон темы письма/уведомления (может содержать плейсхолдеры типа {{username}})", example = "Добро пожаловать, {{username}}!", nullable = true)
    private String subjectTemplate;

    @Schema(description = "Шаблон тела письма/уведомления (может содержать плейсхолдеры)", example = "Спасибо за регистрацию, {{username}}! Ваш код: {{code}}.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bodyTemplate;

    @Schema(description = "Тип шаблона уведомления", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private NotificationTemplateType templateType; // Просто Enum

    @Schema(description = "ID категории события, к которой относится шаблон", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer eventCategoryId;

    @Schema(description = "Имя категории события (только для чтения в Response DTO)", example = "Системные", accessMode = Schema.AccessMode.READ_ONLY)
    private String eventCategoryName;

    @Schema(description = "Каналы доставки по умолчанию для этого шаблона", example = "[\"EMAIL\", \"ON_SITE\"]", nullable = true)
    private List<String> defaultChannels;
}