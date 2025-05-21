package NSU.PetHost.ContentService.repositories;

import NSU.PetHost.ContentService.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    List<Statistics> findAllByAnimal_IdAndDate(long animalId, LocalDate date);

    void deleteByAnimal_Id(long animalId);

}
