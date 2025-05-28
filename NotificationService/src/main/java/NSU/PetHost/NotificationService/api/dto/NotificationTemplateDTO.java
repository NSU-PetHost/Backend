package NSU.PetHost.NotificationService.api.dto;

import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTemplateType;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.List;

@Data
public class NotificationTemplateDTO {
    private Long id;
    @NotBlank
    private String name;
    private String subjectTemplate;
    @NotBlank
    private String bodyTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", nullable = false, columnDefinition = "notification_template_type")
    private NotificationTemplateType templateType;

    @NotNull
    private Integer eventCategoryId;
    private List<String> defaultChannels;
}