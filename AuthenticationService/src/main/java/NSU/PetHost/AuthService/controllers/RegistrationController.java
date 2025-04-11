package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.ConfirmEmailDTO;
import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.requests.VerifyAccountDTO;
import NSU.PetHost.AuthService.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.registration(registrationDTO, bindingResult));

    }

    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmEmailDTO confirmEmailDTO, BindingResult bindingResult) {

        String email = confirmEmailDTO.getEmail();

        System.out.println("email = " + email);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(registrationService.confirmEmail(email, bindingResult));
    }

    @PostMapping("/confirmCode")
    public ResponseEntity<?> confirmCode(@RequestBody @Valid VerifyAccountDTO verifyAccountDTO, BindingResult bindingResult) {

         return ResponseEntity
                 .ok()
                 .body(registrationService.confirmCode(verifyAccountDTO, bindingResult));

    }

}
