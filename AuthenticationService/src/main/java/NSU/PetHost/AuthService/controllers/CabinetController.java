package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.responses.positive.CabinetResponse;
import NSU.PetHost.AuthService.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Schema(description = "Получение личных данных пользователя")
@SecurityRequirement(name = "JWT")
public class CabinetController {

    private final PersonService personService;

    @Operation(
            summary = "Данные о пользователе",
            description = "Позволяет получить более подробную информацию о пользователе"
    )
    @GetMapping("/info/")
    public ResponseEntity<CabinetResponse> getCabinet() {

        return ResponseEntity
                .ok()
                .body(personService.getCabinet());
    }

}
