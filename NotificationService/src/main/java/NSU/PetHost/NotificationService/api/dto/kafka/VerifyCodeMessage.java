package NSU.PetHost.NotificationService.api.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeMessage {
    private String email;
    private String code; // Или int, если код числовой
    // private Long personId; // если сервис авторизации может это отправлять
}