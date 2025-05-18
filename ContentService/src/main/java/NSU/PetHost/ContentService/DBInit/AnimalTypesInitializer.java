package NSU.PetHost.ContentService.DBInit;

import NSU.PetHost.ContentService.models.AnimalsTypes;
import NSU.PetHost.ContentService.repositories.AnimalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnimalTypesInitializer {

    private final AnimalTypeRepository animalTypeRepository;

    public void initAnimalTypes() {

        if (animalTypeRepository.count() == 0) {
            animalTypeRepository.saveAll(List.of(
                            new AnimalsTypes("Cat"),
                            new AnimalsTypes("Dog"),
                            new AnimalsTypes("Telepuzik")
                    )
            );
        }
    }
}
