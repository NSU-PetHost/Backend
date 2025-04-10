package NSU.PetHost.AuthService.dto.responses.positive;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

//пустой, но сделано, чтобы можно было в легко добавить какую-то инфу при регистрации
@Data
@AllArgsConstructor
public class RegistrationResponse {

    @NotBlank
    private String message;

}
