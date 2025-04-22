package NSU.PetHost.AuthService.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Сущность для авторизации")
public class AuthenticationDTO {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    @Schema(description = "Почта пользователя", example = "a.kardash@g.nsu.ru")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @Schema(description = "Пароль пользователя", example = "1234")
    @Min(4)
    private String password;

}
