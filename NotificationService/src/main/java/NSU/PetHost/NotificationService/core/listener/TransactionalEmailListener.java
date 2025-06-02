package NSU.PetHost.NotificationService.core.listener;

import NSU.PetHost.NotificationService.api.dto.kafka.VerifyCodeMessage;
import NSU.PetHost.NotificationService.core.service.EmailService;
import NSU.PetHost.NotificationService.core.service.client.PersonServiceClient;
import NSU.PetHost.proto.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionalEmailListener {
    private final EmailService emailService;
    private final PersonServiceClient personServiceClient; // для получения данных пользователя

    @Autowired
    public TransactionalEmailListener(EmailService emailService, PersonServiceClient personServiceClient) {
        this.emailService = emailService;
        this.personServiceClient = personServiceClient;
    }

    @KafkaListener(
            topics = "confirmMail",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "verifyCodeMessageKafkaListenerContainerFactory" // используем кастомную фабрику
    )
    public void handleConfirmMailEvent(ConsumerRecord<String, VerifyCodeMessage> record) {
        VerifyCodeMessage payload = record.value();
        if (payload == null || payload.getEmail() == null || payload.getCode() == null) {
            log.error("Received invalid VerifyCodeMessage in confirmMail: {}", record.value());
            return;
        }
//        logger.info("Received confirmMail event for email: {}", payload.getEmail());

        // опционально: получить имя пользователя для персонализации
        String username = personServiceClient.getPersonByEmail(payload.getEmail())
                .map(PersonResponse::getUsername) // или getFirstName()
                .orElse("Person"); // Имя по умолчанию, если пользователь не найден или нет имени

        String subject = "PetHost Account - Confirm Your Email";
        String text = String.format("Hello %s,\n\nYour registration confirmation code is: %s\n\nThanks,\nThe PetHost Team",
                username,
                payload.getCode());

        emailService.sendEmail(payload.getEmail(), subject, text);
    }

    @KafkaListener(
            topics = "confirmPassword",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "verifyCodeMessageKafkaListenerContainerFactory"
    )
    public void handleResetPasswordEvent(ConsumerRecord<String, VerifyCodeMessage> record) {
        VerifyCodeMessage payload = record.value();
         if (payload == null || payload.getEmail() == null || payload.getCode() == null) {
             log.error("Received invalid VerifyCodeMessage in confirmPassword: {}", record.value());
            return;
        }

        String username = personServiceClient.getPersonByEmail(payload.getEmail())
                .map(PersonResponse::getUsername)
                .orElse("Person");

        String subject = "PetHost Account - Password Reset";
        String text = String.format("Hello %s,\n\nYour password reset code is: %s\n\nIf you did not request this, please ignore this email.\n\nThanks,\nThe PetHost Team",
                username,
                payload.getCode());

        emailService.sendEmail(payload.getEmail(), subject, text);
    }
}