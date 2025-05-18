package NSU.PetHost.ContentService.DBInit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainInitializer implements CommandLineRunner {

    private final AnimalTypesInitializer animalTypesInitializer;

    @Override
    public void run(String... args) {
        animalTypesInitializer.initAnimalTypes();
    }
}
