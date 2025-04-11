package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.CabinetDTO;
import NSU.PetHost.AuthService.dto.responses.positive.CabinetResponse;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.services.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class CabinetController {

    private final PersonService personService;

    @GetMapping("/info/{id}")
    public ResponseEntity<CabinetResponse> getCabinet(@PathVariable long id) {

        return ResponseEntity
                .ok()
                .body(personService.getCabinet(id));
    }

}
