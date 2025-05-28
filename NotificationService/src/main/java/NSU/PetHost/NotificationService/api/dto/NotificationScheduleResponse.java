package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetTypeConverter;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
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

//@Data
//@NoArgsConstructor
//@Builder
//@AllArgsConstructor
//public class NotificationScheduleResponse {
//    private Long id;
//    private String name;
//    private Long notificationTemplateId;
//    private Long createdByUserId;
//    @Convert(converter = NotificationTargetTypeConverter.class)
//    @Column(name = "target_type", nullable = false, columnDefinition = "notification_target_type")
//    private NotificationTargetType targetType;
//    private Long targetUserId;
//    private String cronExpression;
//    private OffsetDateTime startDatetime;
//    private OffsetDateTime endDatetime;
//    private String timezone;
//    private boolean isActive;
//    private List<String> targetChannels;
//    private OffsetDateTime createdAt;
//    private OffsetDateTime updatedAt;
//}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationScheduleResponse {
    private Long id;
    private String name;
    private NotificationTemplateDTO notificationTemplate;
    private Long createdByUserId;
    private NotificationTargetType targetType;
    private Long targetUserId;
    private String cronExpression;
    private OffsetDateTime startDatetime;
    private OffsetDateTime endDatetime;
    private String timezone;
    private boolean isActive;
    private List<String> targetChannels;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastTriggeredAtByScheduler;
}