package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.responses.positive.RegistrationResponse;
import NSU.PetHost.AuthService.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {
        return ResponseEntity.accepted().body(registrationService.registration(registrationDTO, bindingResult));
    }

}
