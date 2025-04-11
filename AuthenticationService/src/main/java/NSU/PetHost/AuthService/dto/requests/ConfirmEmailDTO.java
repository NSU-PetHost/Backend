package NSU.PetHost.AuthService.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmEmailDTO {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    String email;
}
