package NSU.PetHost.AuthService.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDTO {

    @NotBlank
    @Schema(description = "Refresh JWT токен без 'Bearer '", example = "some.refresh.token")
    private String refreshToken;

}
