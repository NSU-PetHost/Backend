package NSU.PetHost.AuthService.exceptions.Person;

import lombok.Getter;

import java.util.Map;

@Getter
public class PersonNotCreatedException extends RuntimeException {

    private final Map<String, Object> errors;

    public PersonNotCreatedException(Map<String, Object> errors) {
        this.errors = errors;
    }
}
