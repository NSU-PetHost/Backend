package NSU.PetHost.AuthService.exceptions.Person;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(String message) {
        super(message);
    }

}
