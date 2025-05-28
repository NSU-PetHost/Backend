package NSU.PetHost.NotificationService.core.model;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetTypeConverter;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "notification_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_template_id", nullable = false)
    private NotificationTemplate notificationTemplate;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Convert(converter = NotificationTargetTypeConverter.class)
    @Column(name = "target_type", nullable = false, columnDefinition = "notification_target_type")
//    @Enumerated(EnumType.STRING)
//    @Column(name = "target_type", nullable = false, columnDefinition = "notification_target_type")
    private NotificationTargetType targetType;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "cron_expression", length = 100, nullable = false)
    private String cronExpression;

    @Column(name = "start_datetime")
    private OffsetDateTime startDatetime;

    @Column(name = "end_datetime")
    private OffsetDateTime endDatetime;

    @Column(name = "timezone", length = 100, nullable = false)
    private String timezone;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

//    @Type(ListArrayType.class)
//    @JdbcTypeCode(SqlTypes.ARRAY)
//    @Column(
//            name = "target_channels",
//            nullable = false
//    )
//    private String[] targetChannels;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // НОВОЕ ПОЛЕ для отслеживания последнего запуска этим планировщиком
    @Column(name = "last_triggered_at_by_scheduler")
    private OffsetDateTime lastTriggeredAtByScheduler;

    @Type(JsonBinaryType.class) // Используем тип от hypersistence-utils для JSONB
    @Column(name = "target_channels", columnDefinition = "jsonb")
    private List<String> targetChannels;

    @Transient
    public boolean isSimplePeriodicTask() { // Переименовал для ясности
        return (this.cronExpression == null || this.cronExpression.trim().isEmpty());
    }

}