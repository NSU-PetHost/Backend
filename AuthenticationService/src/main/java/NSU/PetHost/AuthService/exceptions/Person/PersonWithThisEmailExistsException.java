package NSU.PetHost.AuthService.exceptions.Person;

public class PersonWithThisEmailExistsException extends RuntimeException {
    public PersonWithThisEmailExistsException(String message) {
        super(message);
    }
}
