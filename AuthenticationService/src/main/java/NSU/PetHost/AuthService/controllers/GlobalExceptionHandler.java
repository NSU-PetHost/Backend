package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.responses.errors.PersonErrorResponse;
import NSU.PetHost.AuthService.exceptions.Person.*;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotCreatedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException ex) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(ex.getErrors(), System.currentTimeMillis()));
    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handle(PersonNotFoundException e) {
        return ResponseEntity.badRequest().body(new PersonErrorResponse(e.getErrors(), System.currentTimeMillis()));
    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handle(RefreshTokenNotFound e) {
        return ResponseEntity.badRequest().body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(PersonWithThisEmailExistsException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonWithThisEmailExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(PersonWithThisNicknameExistsException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonWithThisNicknameExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(SignatureVerificationException.class)
    private ResponseEntity<PersonErrorResponse> handleException(SignatureVerificationException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", "Signature verification failed"), System.currentTimeMillis()));
    }

    @ExceptionHandler(ConfirmEmailException.class)
    private ResponseEntity<PersonErrorResponse> handleException(ConfirmEmailException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(e.getErrors(), System.currentTimeMillis()));
    }

}
