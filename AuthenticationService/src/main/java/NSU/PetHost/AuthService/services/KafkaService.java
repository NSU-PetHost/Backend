package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.VerifyCode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class KafkaService {

    private final KafkaTemplate<Long, VerifyCode> verifyCodeKafkaTemplate;

    public void addVerifyCodeInConfirmMailTopic(VerifyCode verifyCode) {
        verifyCodeKafkaTemplate.send("confirmMail", verifyCode);
    }

    public void addVerifyCodeInConfirmPasswordTopic(VerifyCode verifyCode) {
        verifyCodeKafkaTemplate.send("confirmPassword", verifyCode);
    }

}
