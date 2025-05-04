package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.AuthenticationDTO;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.services.LoginService;
import NSU.PetHost.AuthService.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Schema(description = "Авторизация пользователя")
public class PersonController {

    private final LoginService loginService;
    private final PersonService personService;

    //Проверка валидности данных и выдача нового JWT токена
    @PostMapping("/login")
    @Operation(
            summary = "Авторизация",
            description = "Авторизация пользователя, получение Access и Refresh JWT токенов"
    )
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {

        return ResponseEntity
                .accepted()
                .body(loginService.login(authenticationDTO, bindingResult));

    }

    @PostMapping("/resetPassword")
    @Operation(
            summary = "All access. Восстановление пароля пользователя"
    )
    public ResponseEntity<?> resetPassword(@RequestParam @Email @NotNull String email) {
        return ResponseEntity
                .ok()
                .body(personService.resetPassword(email));
    }

    @PostMapping("/changePassword")
    @Operation(
            summary = "All access. Смена пароля пользователя"
    )
    public ResponseEntity<?> changePassword(@RequestParam(defaultValue = "a.kardash@g.nsu.ru") @Email @NotBlank String email, @RequestParam(defaultValue = "1234")@NotBlank String password) {
        return ResponseEntity
                .ok()
                .body(personService.changePassword(email, password));
    }

}
