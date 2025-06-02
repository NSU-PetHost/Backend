package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.ConfirmEmailDTO;
import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.requests.VerifyAccountDTO;
import NSU.PetHost.AuthService.dto.responses.positive.RegistrationResponse;
import NSU.PetHost.AuthService.services.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
@Schema(description = "Регистрация пользователя")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать пользователя, записывает его в БД")
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.registration(registrationDTO, bindingResult));

    }

    @Operation(summary = "Подтверждение почты",
            description = "Для подтверждения почты отправляется письмо на указанный адрес")
    @PostMapping("/confirmEmail")
    public ResponseEntity<RegistrationResponse> confirmEmail(@RequestBody ConfirmEmailDTO confirmEmailDTO, BindingResult bindingResult) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(registrationService.confirmEmail(confirmEmailDTO.getEmail(), bindingResult));
    }

    @Operation(summary = "Подтверждение кода регистрации",
            description = "У пользователя 5 минут с момента отправки сообщения, если он вводит верный код в указанный промежуток времени, то его почта становится подтверждённой")
    @PostMapping("/confirmCode")
    public ResponseEntity<RegistrationResponse> confirmCode(@RequestBody @Valid VerifyAccountDTO verifyAccountDTO, BindingResult bindingResult) {

        return ResponseEntity
                .ok()
                .body(registrationService.confirmCode(verifyAccountDTO, bindingResult));

    }

}
