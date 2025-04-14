package NSU.PetHost.AuthService.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {

    @NotBlank(message = "Name should not be empty")
    @Size(max = 100, message = "Name should be between greater 2 and lower 100 characters")
    @Schema(description = "Имя пользователя", example = "Aleksandr")
    private String firstName;

    @NotBlank(message = "Surname should not be empty")
    @Size(max = 100, message = "Surname should be between greater 2 and lower 100 characters")
    @Schema(description = "Фамилия пользователя", example = "Kardash")
    private String surname;

    @Size(max = 100, message = "Patronymic should be between greater 2 and lower 100 characters")
    @Schema(description = "Отчество пользователя", nullable = true, example = "Vitalievich")
    private String patronymic;

    @NotBlank(message = "Nickname should not be empty")
    @Size(min = 2, max = 100, message = "Surname should be between greater 2 and lower 50 characters")
    @Schema(description = "'Псевдоним' пользователя", example = "bel9sh")
    private String nickname;

    @NotBlank(message = "Password should not be empty")
    @Schema(description = "Пароль пользователя", example = "1234")
    private String password;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    @Schema(description = "Электрон ная почта пользователя", example = "a.kardash@g.nsu.ru")
    private String email;
}
