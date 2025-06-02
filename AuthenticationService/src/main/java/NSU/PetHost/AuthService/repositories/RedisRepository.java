package NSU.PetHost.AuthService.repositories;

import NSU.PetHost.AuthService.models.VerifyCode;

public interface RedisRepository {

    void addVerifyCode(VerifyCode verifyCode);

    void deleteVerifyCode(String email);

    VerifyCode findVerifyCode(String email);

}
