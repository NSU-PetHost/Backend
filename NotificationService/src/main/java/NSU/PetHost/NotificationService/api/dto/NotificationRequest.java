package NSU.PetHost.NotificationService.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Запрос на создание уведомления")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    @Schema(description = "Заголовок уведомления", example = "Важное сообщение")
    private String title;

    @Schema(description = "Текст уведомления", example = "У вас новое сообщение", required = true)
    @NotBlank
    private String message;

    @Schema(description = "Канал отправки (SMS, EMAIL, PUSH, etc)",
             example = "EMAIL", required = true)
    @NotBlank
    private String channel;

    @Schema(description = "ID связанного шаблона (опционально)")
    private Long notificationTemplateId;

    @Schema(description = "ID расписания (опционально)")
    private Long notificationScheduleId;
}