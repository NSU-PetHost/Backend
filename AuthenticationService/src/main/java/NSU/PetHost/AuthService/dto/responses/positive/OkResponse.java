package NSU.PetHost.AuthService.dto.responses.positive;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OkResponse {

    @NotBlank
    private String message;

}
