package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.exceptions.animals.AnimalsTypesNotFound;
import NSU.PetHost.ContentService.models.AnimalsTypes;
import NSU.PetHost.ContentService.repositories.AnimalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class AnimalsTypesService {

    private final AnimalTypeRepository animalTypeRepository;

    public AnimalsTypes getById(long animalTypeId) {
        return animalTypeRepository.findById(animalTypeId).orElseThrow(() -> new AnimalsTypesNotFound("Animals type with id = " + animalTypeId + " not found"));
    }

    public List<AnimalsTypes> getAll() {
        return animalTypeRepository.findAll();
    }
}
