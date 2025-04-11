package NSU.PetHost.AuthService.exceptions.Person;

import NSU.PetHost.AuthService.dto.responses.errors.PersonErrorResponse;
import lombok.Getter;

import java.util.Map;

@Getter
public class ConfirmEmailException extends RuntimeException {

    private Map<String, Object> errors;

    public ConfirmEmailException(Map<String, Object> errors) {
        this.errors = errors;
    }

}
