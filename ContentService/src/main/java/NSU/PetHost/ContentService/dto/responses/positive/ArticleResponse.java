package NSU.PetHost.ContentService.dto.responses.positive;

import java.io.Serializable;
import java.time.LocalDate;

public record ArticleResponse(long id, String title, String text, long imageID, LocalDate createdAt, long authorID) implements Serializable {}
