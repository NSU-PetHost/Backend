package NSU.PetHost.AuthService.exceptions.Person;

public class RefreshTokenNotFound extends RuntimeException {
    public RefreshTokenNotFound(String message) {
        super(message);
    }
}
