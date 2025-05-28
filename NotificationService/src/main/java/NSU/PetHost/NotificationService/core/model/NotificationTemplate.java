package NSU.PetHost.NotificationService.core.model;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTemplateType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;


import java.util.List;

@Entity
@Table(name = "notification_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "subject_template", columnDefinition = "TEXT")
    private String subjectTemplate;

    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Enumerated(EnumType.STRING)
    private NotificationTemplateType templateType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id", nullable = false)
    private EventCategory eventCategory;

    @Type(JsonBinaryType.class) // Используем тип от hypersistence-utils для JSONB
    @Column(name = "default_channels", columnDefinition = "jsonb")
    private List<String> defaultChannels;

}