package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.responses.kafka.KafkaArticleCreated;
import NSU.PetHost.AuthService.dto.responses.kafka.KafkaArticleUpdated;
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
            groupId = "app.1",
            containerFactory = "verifyCodeKafkaListenerContainerFactory"
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
            groupId = "app.1",
            containerFactory = "verifyCodeKafkaListenerContainerFactory"
    )
    public void sendNotifyEmailResetPassword(ConsumerRecord<Long, VerifyCode> record) {

        mailMessage.setTo(record.value().getEmail());
        mailMessage.setSubject("PetHost reset password");
        mailMessage.setText("Reset password confirmation code: " + record.value().getCode());
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    @KafkaListener(
            topics = "articleCreated",
            groupId = "app.1",
            containerFactory = "articleCreatedKafkaListenerContainerFactory"
    )
    public void sendNotifyEmailArticleCreated(ConsumerRecord<Long, KafkaArticleCreated> record) {

        mailMessage.setTo("a.kardash@g.nsu.ru"); //TODO: заглушка
        mailMessage.setSubject("PetHost article created");
        mailMessage.setText("Your article has been created. ID:" + record.value().getArticleID());
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    @KafkaListener(
            topics = "articleUpdated",
            groupId = "app.1",
            containerFactory = "articleUpdatedKafkaListenerContainerFactory"
    )
    public void sendNotifyEmailArticleUpdated(ConsumerRecord<Long, KafkaArticleUpdated> record) {

        mailMessage.setTo("a.kardash@g.nsu.ru"); //TODO: заглушка
        mailMessage.setSubject("PetHost article status updated");
        mailMessage.setText("Your article has been updated. ID:" + record.value().getArticleID() + ", status: " + record.value().getStatus());
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }



}
