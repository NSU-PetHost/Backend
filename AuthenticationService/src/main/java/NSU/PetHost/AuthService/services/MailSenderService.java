package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.VerifyCode;
import NSU.PetHost.AuthService.publishers.EmailPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService implements EmailPublisher {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    private final SimpleMailMessage mailMessage;

    @Override
    @KafkaListener(
            topics = "confirmMail",
            groupId = "app.1"
    )
    public void sendVerifyCodeToEmail(ConsumerRecord<Long, VerifyCode> record) {

        mailMessage.setTo(record.value().getEmail());
        mailMessage.setSubject("PetHost confirm registration");
        mailMessage.setText("Registration confirmation code: " + record.value().getCode());
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    @KafkaListener(
            topics = "confirmPassword",
            groupId = "app.1"
    )
    public void sendNotifyEmailResetPassword(ConsumerRecord<Long, VerifyCode> record) {

        mailMessage.setTo(record.value().getEmail());
        mailMessage.setSubject("PetHost reset password");
        mailMessage.setText("Reset password confirmation code: " + record.value().getCode());
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

}
