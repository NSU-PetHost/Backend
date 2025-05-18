package NSU.PetHost.ContentService.dto.responses.positive;

import java.util.Date;

public record AnimalResponse(String name, Date dateOfBirth, double weight, long imageID) {
}
