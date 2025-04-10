package NSU.PetHost.AuthService.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabinetDTO {

    @NotEmpty(message = "Name should not be empty")
    @Size(max = 100, message = "Name should be between greater 2 and lower 100 characters")
    private String username;

    @NotEmpty(message = "Surname should not be empty")
    @Size(max = 100, message = "Surname should be between greater 2 and lower 100 characters")
    private String surname;

    @Size(max = 100, message = "Patronymic should be between greater 2 and lower 100 characters")
    private String patronymic;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email should not be empty")
    private String email;
}
