package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.dto.requests.CreateAnimalDTO;
import NSU.PetHost.ContentService.dto.responses.positive.AnimalResponse;
import NSU.PetHost.ContentService.dto.responses.positive.AnimalsTypesResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.models.Animals;
import NSU.PetHost.ContentService.repositories.AnimalRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalsService {

    private final AnimalsTypesService animalsTypesService;
    private final AnimalRepository animalRepository;
    private final ImageService imageService;

    public AnimalsTypesResponse getAnimalsTypes() {
        return new AnimalsTypesResponse(animalsTypesService.getAll());
    }

    public OkResponse createAnimal(String name, Date dateOfBirth, Double weight, MultipartFile image, Long petTypeId) {

        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Animals animals = new Animals();
        animals.setName(name);
        animals.setOwnerId(personDetails.getId());
        if (weight != null) animals.setWeight(weight);
        if (dateOfBirth != null) animals.setDateOfBirth(dateOfBirth);
        if (image != null) animals.setImage(imageService.uploadImage(image));
        if (petTypeId != null) animals.setAnimalsType(animalsTypesService.getById(petTypeId));

        animalRepository.save(animals);
        return new OkResponse("Animal created", System.currentTimeMillis());

    }

    public List<Animals> findAllAnimalsByOwnerId(long ownerId) {
        return animalRepository.findAnimalsByOwnerId(ownerId);
    }

    private String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    public List<AnimalResponse> getPets() {
        return findAllAnimalsByOwnerId(((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).stream().map(x -> new AnimalResponse(x.getName(), x.getDateOfBirth(), x.getWeight(), x.getImage().getId())
        ).toList();
    }

}
