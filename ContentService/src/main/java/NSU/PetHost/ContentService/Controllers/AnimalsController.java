package NSU.PetHost.ContentService.Controllers;

import NSU.PetHost.ContentService.dto.requests.CreateAnimalDTO;
import NSU.PetHost.ContentService.dto.responses.positive.AnimalResponse;
import NSU.PetHost.ContentService.dto.responses.positive.AnimalsTypesResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.security.PersonDetails;
import NSU.PetHost.ContentService.services.AnimalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/animals")
public class AnimalsController {

    private final AnimalsService animalsService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OkResponse> createAnimal(@RequestParam(value = "name", defaultValue = "Misha") String name,
                                                   @RequestParam(value = "dateOfBirth", required = false) Date dateOfBirth,
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

}
