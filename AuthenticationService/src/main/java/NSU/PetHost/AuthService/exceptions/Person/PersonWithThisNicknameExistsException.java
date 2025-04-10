package NSU.PetHost.AuthService.exceptions.Person;

public class PersonWithThisNicknameExistsException extends RuntimeException {
    public PersonWithThisNicknameExistsException(String message) {
        super(message);
    }
}
