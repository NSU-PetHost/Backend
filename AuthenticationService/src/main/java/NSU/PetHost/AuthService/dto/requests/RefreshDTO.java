package NSU.PetHost.AuthService.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDTO {

    @NotBlank
    private String refreshToken;

}
