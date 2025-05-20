package NSU.PetHost.ContentService.exceptions.animals;

public class AnimalNotFoundException extends RuntimeException {
    public AnimalNotFoundException(String message) {
        super(message);
    }
}
