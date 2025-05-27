package NSU.PetHost.ContentService.dto.responses.positive;

import java.time.LocalDate;

public record AnimalResponse(long id, String animalType, String name, LocalDate dateOfBirth, double weight, long imageID) {
}
