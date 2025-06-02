package NSU.PetHost.NotificationService.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonJWT {

    private long id;

    private String nickname;

    private String role;

}