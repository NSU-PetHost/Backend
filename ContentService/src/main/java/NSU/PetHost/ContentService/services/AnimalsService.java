package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.dto.responses.positive.AnimalResponse;
import NSU.PetHost.ContentService.dto.responses.positive.AnimalsTypesResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.exceptions.AccessDeniedException;
import NSU.PetHost.ContentService.exceptions.animals.AnimalNotFoundException;
import NSU.PetHost.ContentService.models.Animals;
import NSU.PetHost.ContentService.repositories.AnimalRepository;
import NSU.PetHost.ContentService.repositories.StatisticsRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalsService {

    private final AnimalsTypesService animalsTypesService;
    private final StatisticsRepository statisticsRepository;
    private final AnimalRepository animalRepository;
    private final ImageService imageService;

    public AnimalsTypesResponse getAnimalsTypes() {
        return new AnimalsTypesResponse(animalsTypesService.getAll());
    }

    public Animals getAnimal(@Min(1) long animalId) {
        return animalRepository.findById(animalId).orElseThrow(() -> new AnimalNotFoundException("Animal not found"));
    }

    private boolean hasPermission(Animals animal) {
        return animal.getOwnerId() == ((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public OkResponse createAnimal(String name, LocalDate dateOfBirth, Double weight, MultipartFile image, Long petTypeId) {

        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Animals animals = new Animals();
        animals.setName(name);
        animals.setOwnerId(personDetails.getId());
        if (weight != null) animals.setWeight(weight);
        if (dateOfBirth != null) animals.setDateOfBirth(dateOfBirth);
        if (image != null) animals.setImage(imageService.uploadImage(image, true));
        if (petTypeId != null) animals.setAnimalsType(animalsTypesService.getById(petTypeId));

        animalRepository.save(animals);
        return new OkResponse("Animal created", System.currentTimeMillis());

    }

    public List<Animals> findAllAnimalsByOwnerId(long ownerId) {
        return animalRepository.findAnimalsByOwnerId(ownerId);
    }

    public List<AnimalResponse> getPets() {
        return findAllAnimalsByOwnerId(((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).stream().map(x -> new AnimalResponse(x.getId(), x.getName(), x.getDateOfBirth(), x.getWeight(), x.getImage().getId())
        ).toList();
    }

    public OkResponse deletePet(@Min(1) long animalId) {

        Animals animal = getAnimal(animalId);

        if (animal.getOwnerId() != ((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()) {
            throw new AccessDeniedException("You do not have permission to delete this animal");
        }

        animalRepository.delete(animal);
        statisticsRepository.deleteByAnimal_Id(animalId);

        return new OkResponse("Animal deleted", System.currentTimeMillis());
    }

    public OkResponse updateAnimal(@Min(1) long animalId, @Size(max = 50) String name, LocalDate dateOfBirth, Double weight, MultipartFile image, Long petTypeId) {

        Animals animal = getAnimal(animalId);

        if (!hasPermission(animal)) throw new AccessDeniedException("No permission to update this animal");

        if (name != null) animal.setName(name);
        if (dateOfBirth != null) animal.setDateOfBirth(dateOfBirth);
        if (weight != null) animal.setWeight(weight);
        if (image != null) animal.setImage(imageService.uploadImage(image, true));
        if (petTypeId != null) animal.setAnimalsType(animalsTypesService.getById(petTypeId));
        animalRepository.save(animal);

        return new OkResponse("Animal updated", System.currentTimeMillis());

    }
}
