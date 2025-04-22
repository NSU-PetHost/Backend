package NSU.PetHost.AuthService.dto.responses.positive;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

//пустой, но сделано, чтобы можно было в легко добавить какую-то инфу при регистрации
@Data
@AllArgsConstructor
public class RegistrationResponse {

    @NotBlank
    @Schema(description = "В случае успешной регистрации придёт сообщение 'Registration complete'", example = "Registration complete")
    private String message;

}
