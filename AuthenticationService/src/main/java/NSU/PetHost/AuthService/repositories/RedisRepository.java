package NSU.PetHost.AuthService.repositories;

import NSU.PetHost.AuthService.models.VerifyCode;

public interface RedisRepository {

    void add(VerifyCode verifyCode);

    void delete(String email);

    VerifyCode findVerifyCode(String email);

}
