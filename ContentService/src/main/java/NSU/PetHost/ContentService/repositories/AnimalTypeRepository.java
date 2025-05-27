package NSU.PetHost.ContentService.repositories;

import NSU.PetHost.ContentService.models.AnimalsTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalTypeRepository extends JpaRepository<AnimalsTypes, Long> {



}
