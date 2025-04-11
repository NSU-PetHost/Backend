package NSU.PetHost.AuthService.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyAccountDTO {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    private String email;

    @Min(100000)
    @Max(999999)
    private int verifyCode;

}
