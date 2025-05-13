package NSU.PetHost.AuthService.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCodeErrorResponse {

    private String error;
    private long timestamp;

}
