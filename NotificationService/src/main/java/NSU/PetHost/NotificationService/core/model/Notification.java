package NSU.PetHost.NotificationService.core.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "channel", length = 20, nullable = false)
    private String channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_template_id")
    private NotificationTemplate notificationTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_schedule_id")
    private NotificationSchedule notificationSchedule;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;
}