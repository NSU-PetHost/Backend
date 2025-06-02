package NSU.PetHost.NotificationService.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для категории события")
public class EventCategoryDto {
    @Schema(description = "Название категории события", example = "Новости", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}