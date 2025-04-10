package NSU.PetHost.AuthService.dto.responses.positive;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CabinetResponse {

    @NotBlank
    private final String firstName;
    @NotBlank
    private final String surname;
    @NotBlank
    private final String patronymic;
    @NotBlank
    private final String email;


}
