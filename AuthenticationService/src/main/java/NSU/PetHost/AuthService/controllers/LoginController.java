package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.AuthenticationDTO;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.services.LoginService;
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
@Schema(description = "Авторизация пользователя")
public class LoginController {

    private final LoginService loginService;

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

}
