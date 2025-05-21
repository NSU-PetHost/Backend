package NSU.PetHost.AuthService.publishers;

import NSU.PetHost.AuthService.models.VerifyCode;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface EmailPublisher {

    void sendVerifyCodeToEmail(ConsumerRecord<Long, VerifyCode> record);

    void sendNotifyEmailResetPassword(ConsumerRecord<Long, VerifyCode> record);

}
