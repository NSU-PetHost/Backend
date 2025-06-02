package NSU.PetHost.ContentService.DBInit;

import NSU.PetHost.ContentService.repositories.AnimalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainInitializer implements CommandLineRunner {

    private final AnimalTypesInitializer animalTypesInitializer;
    private final RefusalReasonsInitializer refusalReasonsInitializer;

    @Override
    public void run(String... args) {
        animalTypesInitializer.initAnimalTypes();
        refusalReasonsInitializer.initRefusalReasons();
    }
}
