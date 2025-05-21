package NSU.PetHost.ContentService.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private Map<String, Object> errors;

}
