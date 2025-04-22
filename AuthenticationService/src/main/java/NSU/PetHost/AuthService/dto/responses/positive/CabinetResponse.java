package NSU.PetHost.AuthService.dto.responses.positive;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CabinetResponse {

    @Schema(description = "Имя пользователя", example = "Aleksandr")
    private String username;

    @Schema(description = "Фамилия пользователя", example = "Kardash")
    private String surname;

    @Schema(description = "Отчество пользователя", nullable = true, example = "Vitalievich")
    private String patronymic;

    @Schema(description = "Адрес электронной почты пользователя", example = "a.kardash@g.nsu.ru")
    private String email;

}
