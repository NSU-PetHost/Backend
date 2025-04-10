package NSU.PetHost.AuthService.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDTO {

    @NotEmpty
    private String refreshToken;

}
