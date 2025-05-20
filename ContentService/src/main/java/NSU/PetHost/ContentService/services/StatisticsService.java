package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.dto.responses.positive.StatisticsResponse;
import NSU.PetHost.ContentService.exceptions.AccessDeniedException;
import NSU.PetHost.ContentService.models.Animals;
import NSU.PetHost.ContentService.models.Statistics;
import NSU.PetHost.ContentService.repositories.StatisticsRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final AnimalsService animalsService;

    private boolean hasPermission(Animals animal) {
        return animal.getOwnerId() == ((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public List<StatisticsResponse> getStatistics(@Min(1) long petId, LocalDate localDate) {

        Animals animals = animalsService.getAnimal(petId);

        if (!hasPermission(animals))
            throw new AccessDeniedException("You do not have permission to access this resource");

        return statisticsRepository.findAllByAnimal_IdAndDate(petId, localDate).stream().map(this::convertStatisticsToStatResponse).toList();
    }

    private StatisticsResponse convertStatisticsToStatResponse(Statistics statistics) {
        return new StatisticsResponse(
                statistics.getAppetite(),
                statistics.getThirst(),
                statistics.getActivity(),
                statistics.getGastrointestinalTract(),
                statistics.getNote()
        );
    }

    public OkResponse createStatistics(@Min(1) long animalID,
                                       @Min(0) @Max(10) int appetite,
                                       @Min(0) @Max(10) int thirst,
                                       @Min(0) @Max(10) int activity,
                                       @Min(0) @Max(10) int gastrointestinalTract,
                                       LocalDate date,
                                       String note) {


        Animals animals = animalsService.getAnimal(animalID);

        if (!hasPermission(animals))
            throw new AccessDeniedException("You do not have permission to access this resource");

        Statistics statistics = new Statistics();
        statistics.setAnimal(animals);
        statistics.setAppetite(appetite);
        statistics.setThirst(thirst);
        statistics.setActivity(activity);
        statistics.setGastrointestinalTract(gastrointestinalTract);
        if (note != null && !note.isEmpty()) statistics.setNote(note);
        if (date != null) statistics.setDate(date);
        else statistics.setDate(LocalDate.now());

        statisticsRepository.save(statistics);

        return new OkResponse("Statistics created", System.currentTimeMillis());
    }
}
