package NSU.PetHost.ContentService.repositories;

import NSU.PetHost.ContentService.models.Animals;
import NSU.PetHost.ContentService.models.AnimalsTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animals, Long> {

    List<Animals> findAnimalsByOwnerId(Long ownerId);

}
