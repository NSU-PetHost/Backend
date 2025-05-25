package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetTypeConverter;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
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
// import java.util.List; // если используете List<String>

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationScheduleRequest {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Notification template ID cannot be null")
    private Long notificationTemplateId;

    private Long createdByUserId;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "template_type", nullable = false, columnDefinition = "notification_template_type")
    @Convert(converter = NotificationTargetTypeConverter.class)
    @Column(name = "target_type", nullable = false, columnDefinition = "notification_target_type")
    private NotificationTargetType targetType;

    private Long targetUserId;

    @NotBlank(message = "Cron expression cannot be blank")
    private String cronExpression;

    private OffsetDateTime startDatetime;

    private OffsetDateTime endDatetime;

    @NotBlank(message = "Timezone cannot be blank")
    private String timezone;

    private boolean isActive = true;

    // Если targetChannels - это List<String> и он должен быть непустым
    // @NotEmpty(message = "Target channels cannot be empty if provided")
    // @Size(min=1, message = "At least one target channel must be specified if array is not null")
    private List<String> targetChannels; // Или List<String> targetChannels;
}