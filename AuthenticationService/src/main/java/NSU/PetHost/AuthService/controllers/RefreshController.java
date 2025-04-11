package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.RefreshDTO;
import NSU.PetHost.AuthService.dto.responses.errors.PersonErrorResponse;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.exceptions.Person.RefreshTokenNotFound;
import NSU.PetHost.AuthService.services.RefreshService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshService refreshService;

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshDTO refreshDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(refreshService.refresh(refreshDTO, bindingResult));

    }
}
