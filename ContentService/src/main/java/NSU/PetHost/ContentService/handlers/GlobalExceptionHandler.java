package NSU.PetHost.ContentService.handlers;

import NSU.PetHost.ContentService.dto.responses.errors.ErrorResponse;
import NSU.PetHost.ContentService.exceptions.AccessDeniedException;
import NSU.PetHost.ContentService.exceptions.Animals.AnimalsTypesNotFound;
import NSU.PetHost.ContentService.exceptions.Images.ImageNotFoundException;
import NSU.PetHost.ContentService.exceptions.Images.SaveImageException;
import NSU.PetHost.ContentService.exceptions.InternalServerException;
import NSU.PetHost.ContentService.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaveImageException.class)
    public ResponseEntity<ErrorResponse> handleSaveImageException(SaveImageException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(AnimalsTypesNotFound.class)
    public ResponseEntity<ErrorResponse> handleSaveImageException(AnimalsTypesNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }


    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSaveImageException(ImageNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(InternalServerException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(Map.of("error", ex.getMessage()), System.currentTimeMillis()));
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> InternalServerException(ValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getErrors(), System.currentTimeMillis()));
    }
}
