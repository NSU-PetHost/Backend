package NSU.PetHost.AuthService.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyAccountDTO {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    @Schema(description = "Электронная почта пользователя", example = "a.kardash@g.nsu.ru")
    private String email;

    @Min(100000)
    @Max(999999)
    @Schema(description = "Проверочный код, присылаемый пользователю на почту", example = "123456")
    private int verifyCode;

}
