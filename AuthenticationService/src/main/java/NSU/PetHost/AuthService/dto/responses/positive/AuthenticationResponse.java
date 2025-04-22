package NSU.PetHost.AuthService.dto.responses.positive;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {

    @NotBlank
    @Schema(description = "Access JWT токен для авторизации на сервисах", example = "some.access.token")
    private String accessToken;

    @NotBlank
    @Schema(description = "Refresh JWT токен для восстановления Access и Refresh JWT токенов", example = "some.refresh.token")
    private String refreshToken;
}
