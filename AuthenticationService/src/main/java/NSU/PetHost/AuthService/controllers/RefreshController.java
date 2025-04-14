package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.RefreshDTO;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.services.RefreshService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Schema(description = "Обновление Refresh JWT токена")
public class RefreshController {

    private final RefreshService refreshService;

    @PostMapping("/refresh")
    @Operation( summary = "Обновление Refresh JWT токена",
            description = "Позволяет обновить JWT токен на новый токен, действующий неделю")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshDTO refreshDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(refreshService.refresh(refreshDTO, bindingResult));

    }
}
