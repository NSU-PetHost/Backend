package NSU.PetHost.AuthService.exceptions.Person;

import lombok.Getter;

import java.util.Map;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(String message) {
        super(message);
    }

}
