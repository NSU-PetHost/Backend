package NSU.PetHost.AuthService.util.exceptions.Person;

public class PersonWithThisEmailExistsException extends RuntimeException {
    public PersonWithThisEmailExistsException(String message) {
        super(message);
    }
}
