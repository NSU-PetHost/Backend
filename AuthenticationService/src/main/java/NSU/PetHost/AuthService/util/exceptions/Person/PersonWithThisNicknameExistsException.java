package NSU.PetHost.AuthService.util.exceptions.Person;

public class PersonWithThisNicknameExistsException extends RuntimeException {
    public PersonWithThisNicknameExistsException(String message) {
        super(message);
    }
}
