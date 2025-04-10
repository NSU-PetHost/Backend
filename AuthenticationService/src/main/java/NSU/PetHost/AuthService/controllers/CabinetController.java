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

    // TODO: /auth/info/id для реализации инфы о пользователе через officeDTO
    @GetMapping("/info/{id}")
    public ResponseEntity<CabinetResponse> getCabinet(@PathVariable long id) {

        Person person = personService.getPersonById(id);

        return ResponseEntity.accepted()
                .body(new CabinetResponse(
                        person.getFirstName(),
                        person.getSurname(),
                        person.getPatronymic(),
                        person.getEmail()
                ));

    }

}
