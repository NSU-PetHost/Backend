package NSU.PetHost.AuthService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationDTO {


    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email should not be empty")
    private String email;

    @NotEmpty(message = "Nickname should not be empty")
    @Size(min = 2, max = 100, message = "Surname should be between greater 2 and lower 50 characters")
    private String nickname;

    @NotEmpty(message = "Password should not be empty")
    private String password;

}
