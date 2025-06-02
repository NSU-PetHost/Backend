package NSU.PetHost.NotificationService.api.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeMessage {
    private String email;
    private String code;
}