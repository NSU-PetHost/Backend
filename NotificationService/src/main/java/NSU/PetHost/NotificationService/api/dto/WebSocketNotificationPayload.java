package NSU.PetHost.NotificationService.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotificationPayload {
    private Long notificationId;
    private String title;
    private String message;
    private String channel; // Будет "ON_SITE"
    private OffsetDateTime createdAt;
    private boolean isRead; // Изначально false
}