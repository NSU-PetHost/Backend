package NSU.PetHost.NotificationService.core.error;

import NSU.PetHost.NotificationService.api.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest; // jakarta для Spring Boot 3+
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Validation Error", "Input validation failed", request.getRequestURI(), details);
        log.warn("Validation error for path {}: {}", request.getRequestURI(), details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenAccessException(
            ForbiddenAccessException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                "Forbidden", ex.getMessage(), request.getRequestURI(), null);
        log.warn("Forbidden access for path {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                "Resource Not Found", ex.getMessage(), request.getRequestURI(), null);
        log.warn("Resource not found for path {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRequestArgumentException(
            InvalidRequestArgumentException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument", ex.getMessage(), request.getRequestURI(), null);
        log.warn("Invalid argument for path {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // для ошибок типа "неверный тип параметра", например, строка вместо Long
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
         List<String> details = new ArrayList<>();
         details.add(ex.getCause() != null ? ex.getCause().getMessage() : "Invalid type");

        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch", message, request.getRequestURI(), details);
        log.warn("Type mismatch for path {}: {}", request.getRequestURI(), message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // для пропущенных обязательных параметров запроса
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = String.format("Required request parameter '%s' for method parameter type %s is not present",
                                       ex.getParameterName(), ex.getParameterType());
        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Missing Parameter", message, request.getRequestURI(), null);
        log.warn("Missing parameter for path {}: {}", request.getRequestURI(), message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // общий обработчик
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error for path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", "An unexpected error occurred. Please try again later.", request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}