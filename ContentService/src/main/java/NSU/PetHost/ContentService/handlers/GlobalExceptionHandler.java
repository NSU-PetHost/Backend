package NSU.PetHost.ContentService.handlers;

import NSU.PetHost.ContentService.dto.responses.errors.ErrorResponse;
import NSU.PetHost.ContentService.exceptions.AccessDeniedException;
import NSU.PetHost.ContentService.exceptions.InternalServerException;
import NSU.PetHost.ContentService.exceptions.ValidationException;
import NSU.PetHost.ContentService.exceptions.animals.AnimalNotFoundException;
import NSU.PetHost.ContentService.exceptions.animals.AnimalsTypesNotFound;
import NSU.PetHost.ContentService.exceptions.articles.ArticlesNotFoundException;
import NSU.PetHost.ContentService.exceptions.images.DeleteImageException;
import NSU.PetHost.ContentService.exceptions.images.ImageNotFoundException;
import NSU.PetHost.ContentService.exceptions.images.SaveImageException;
import NSU.PetHost.ContentService.exceptions.refusalReasons.RefusalReasonNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaveImageException.class)
    public ResponseEntity<ErrorResponse> handleSaveImageException(SaveImageException ex) {
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(AnimalsTypesNotFound.class)
    public ResponseEntity<ErrorResponse> handleSaveImageException(AnimalsTypesNotFound ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }


    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSaveImageException(ImageNotFoundException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(InternalServerException ex) {
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(AccessDeniedException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(ValidationException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(ex.getErrors(), System.currentTimeMillis()));
    }

    @ExceptionHandler(RefusalReasonNotFoundException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(RefusalReasonNotFoundException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }


    @ExceptionHandler(AnimalNotFoundException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(AnimalNotFoundException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(ArticlesNotFoundException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(ArticlesNotFoundException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(DeleteImageException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(DeleteImageException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }
}
