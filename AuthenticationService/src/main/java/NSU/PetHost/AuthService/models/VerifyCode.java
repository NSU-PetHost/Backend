package NSU.PetHost.AuthService.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCode implements Serializable {

    private String email;

    private int code;

    private boolean verified;

}
