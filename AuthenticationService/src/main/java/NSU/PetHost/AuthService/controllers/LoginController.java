package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.requests.AuthenticationDTO;
import NSU.PetHost.AuthService.dto.responses.errors.PersonErrorResponse;
import NSU.PetHost.AuthService.dto.responses.positive.AuthenticationResponse;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.services.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    //Проверка валидности данных и выдача нового JWT токена
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {

        return ResponseEntity.ok().body(loginService.login(authenticationDTO, bindingResult));

    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handle(PersonNotFoundException e) {
        return ResponseEntity.badRequest().body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

}
