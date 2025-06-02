package NSU.PetHost.ContentService.Controllers;

import NSU.PetHost.ContentService.dto.responses.positive.AnimalResponse;
import NSU.PetHost.ContentService.dto.responses.positive.AnimalsTypesResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.dto.responses.positive.StatisticsResponse;
import NSU.PetHost.ContentService.services.AnimalsService;
import NSU.PetHost.ContentService.services.StatisticsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/animals")
@Validated
public class AnimalsController {

    private final AnimalsService animalsService;
    private final StatisticsService statisticsService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OkResponse> createAnimal(@RequestParam(value = "name", defaultValue = "Misha") @Size(max = 50) String name,
                                                   @RequestParam(value = "dateOfBirth", required = false) LocalDate dateOfBirth,
                                                   @RequestParam(value = "weight", required = false) Double weight,
                                                   @RequestParam(value = "image", required = false) MultipartFile image,
                                                   @RequestParam(value = "petTypeId", defaultValue = "1") Long petTypeId) {
        return ResponseEntity
                .ok(animalsService.createAnimal(name, dateOfBirth, weight, image, petTypeId));
    }

    @GetMapping("/getPets")
    public ResponseEntity<List<AnimalResponse>> getPets() {

        return ResponseEntity
                .ok(animalsService.getPets());
    }

    @GetMapping("/getTypes")
    public ResponseEntity<AnimalsTypesResponse> getAnimalTypes() {
        return ResponseEntity
                .ok(animalsService.getAnimalsTypes());
    }

    @PostMapping("/deleteAnimal")
    public ResponseEntity<OkResponse> deleteAnimal(@RequestParam(defaultValue = "1") @Min(1) long animalId) {

        return ResponseEntity
                .ok(animalsService.deletePet(animalId));

    }

    @PostMapping("/createStatistics")
    public ResponseEntity<OkResponse> createStatistics(@RequestParam @Min(1) long animalID,
                                                       @RequestParam @Min(0) @Max(10) int appetite,
                                                       @RequestParam @Min(0) @Max(10) int thirst,
                                                       @RequestParam @Min(0) @Max(10) int activity,
                                                       @RequestParam @Min(0) @Max(10) int gastrointestinalTract,
                                                       @RequestParam(required = false) LocalDate date,
                                                       @RequestParam(required = false) String note) {

        return ResponseEntity
                .ok(statisticsService.createStatistics(animalID, appetite, thirst, activity, gastrointestinalTract, date, note));

    }

    @GetMapping("/getStatistics")
    public ResponseEntity<List<StatisticsResponse>> getStatistics(@RequestParam @Min(1) long animalID,
                                                                  @RequestParam LocalDate date) {

        return ResponseEntity
                .ok(statisticsService.getStatistics(animalID, date));

    }

    @PutMapping("/updateAnimal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OkResponse> updateAnimal(@RequestParam(defaultValue = "1") @Min(1) long animalId,
                                                   @RequestParam(value = "name", defaultValue = "Misha", required = false) @Size(max = 50) String name,
                                                   @RequestParam(value = "dateOfBirth", required = false) LocalDate dateOfBirth,
                                                   @RequestParam(value = "weight", required = false) Double weight,
                                                   @RequestParam(value = "image", required = false) MultipartFile image,
                                                   @RequestParam(value = "petTypeId", defaultValue = "1", required = false) Long petTypeId) {

        return ResponseEntity
                .ok(animalsService.updateAnimal(animalId, name, dateOfBirth, weight, image, petTypeId));

    }

}
