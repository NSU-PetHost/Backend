package NSU.PetHost.AuthService.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class PersonErrorResponse {

    private Map<String, Object> errors;
    private long timestamp;

}
