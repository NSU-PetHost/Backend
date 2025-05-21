package NSU.PetHost.ContentService.exceptions.articles;

public class ArticlesNotFoundException extends RuntimeException {
    public ArticlesNotFoundException(String message) {
        super(message);
    }
}
