package NSU.PetHost.AuthService.controllers;

import NSU.PetHost.AuthService.dto.responses.errors.PersonErrorResponse;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotCreatedException;
import NSU.PetHost.AuthService.exceptions.Person.PersonWithThisEmailExistsException;
import NSU.PetHost.AuthService.exceptions.Person.PersonWithThisNicknameExistsException;
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

    @ExceptionHandler(PersonWithThisEmailExistsException.class)
    private ResponseEntity<Map<String, Object>> handleException(PersonWithThisEmailExistsException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("errors", ex.getMessage()));
    }

    @ExceptionHandler(PersonWithThisNicknameExistsException.class)
    private ResponseEntity<Map<String, Object>> handleException(PersonWithThisNicknameExistsException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("errors", ex.getMessage()));
    }

    @ExceptionHandler(SignatureVerificationException.class)
    private ResponseEntity<Map<String, Object>> handleException(SignatureVerificationException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("errors", "Signature verification failed"));
    }

}
