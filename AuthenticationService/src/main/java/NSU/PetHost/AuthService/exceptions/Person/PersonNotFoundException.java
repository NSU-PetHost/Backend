package NSU.PetHost.AuthService.exceptions.Person;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PersonNotFoundException extends RuntimeException {

    Map<String, Object> errors = new HashMap<>();

    public PersonNotFoundException(Map<String, Object> errors) {
        this.errors = errors;
    }

}
