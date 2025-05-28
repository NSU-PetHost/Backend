package NSU.PetHost.NotificationService.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestArgumentException extends RuntimeException {
    public InvalidRequestArgumentException(String message) {
        super(message);
    }
}