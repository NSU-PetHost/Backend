package NSU.PetHost.AuthService.handlers;

import NSU.PetHost.AuthService.dto.responses.errors.PersonErrorResponse;
import NSU.PetHost.AuthService.exceptions.Person.*;
import NSU.PetHost.AuthService.exceptions.Roles.RoleNotFoundException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
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

    @ExceptionHandler(SQLException.class)
    private ResponseEntity<PersonErrorResponse> handleException(SQLException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage().substring(e.getMessage().indexOf("Detail:") + 8)), System.currentTimeMillis()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(AuthorizationDeniedException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    private ResponseEntity<PersonErrorResponse> handleException(CustomAccessDeniedException e) {
        return ResponseEntity
                .badRequest()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    private ResponseEntity<PersonErrorResponse> handleException(RoleNotFoundException e) {
        return ResponseEntity
                .internalServerError()
                .body(new PersonErrorResponse(Map.of("error", e.getMessage()), System.currentTimeMillis()));
    }

}
