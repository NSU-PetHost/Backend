package NSU.PetHost.NotificationService.core.service;

import NSU.PetHost.NotificationService.api.dto.WebSocketNotificationPayload;
import NSU.PetHost.NotificationService.core.model.Notification;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationChannel;
import NSU.PetHost.NotificationService.core.repository.NotificationRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationScheduleRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationTemplateRepository;
import NSU.PetHost.NotificationService.core.service.client.PersonServiceClient;
import NSU.PetHost.proto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationCreationService {
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationScheduleRepository scheduleRepository;
    private final EmailService emailService;
    private final PersonServiceClient personServiceClient;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public Notification createAndSendNotification(
            Long personId,
            String title,
            String message,
            NotificationChannel channel,
            Long templateId, Long scheduleId
    ) {
        Notification notification = new Notification();
        notification.setPersonId(personId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setChannel(channel.name());

        if (templateId != null) {
            templateRepository.findById(templateId).ifPresent(notification::setNotificationTemplate);
        }
        if (scheduleId != null) {
            scheduleRepository.findById(scheduleId).ifPresent(notification::setNotificationSchedule);
        }

        Notification savedNotification = notificationRepository.save(notification); // сначала сохраняем, потом пытаемся отправить

        // отправляем асинхронно, чтобы не блокировать вызывающий поток (например, Quartz Job)
        triggerActualSending(savedNotification, personId, title, message, channel);

        return savedNotification;
    }

    @Async("asyncNotificationExecutor")
    public void triggerActualSending(Notification notification, Long personId, String title, String message, NotificationChannel channel) {
        log.info("Async triggerActualSending for notification ID: {} to user {}", notification.getId(), personId);
        if (channel == NotificationChannel.ON_SITE) {
            // Формируем payload для WebSocket
            WebSocketNotificationPayload wsPayload = new WebSocketNotificationPayload(
                    notification.getId(),
                    title,
                    message,
                    channel.name(),
                    notification.getCreatedAt(),
                    notification.isRead()
            );

            // Определяем "пользовательский" топик. Клиент должен быть подписан на /user/queue/notifications
            String userDestination = "/user/" + personId.toString() + "/queue/notifications";

            try {
                messagingTemplate.convertAndSend(userDestination, wsPayload);
                log.info("ON_SITE notification (ID: {}) sent via WebSocket to user {} at destination: {}",
                        notification.getId(), personId, userDestination);
            } catch (Exception e) {
                log.error("Failed to send WebSocket notification for ID {} to user {}: {}",
                        notification.getId(), personId, e.getMessage(), e);
            }
        } else if (channel == NotificationChannel.EMAIL) {
            log.info("Attempting to send EMAIL to user {}: {}", personId, title);
            personServiceClient.getPersonById(personId).ifPresentOrElse(user -> {
                user.getEmail();
                if (!user.getEmail().isEmpty()) {
                    boolean success = emailService.sendEmail(user.getEmail(), title, message);
                    if (!success) {
                        log.warn("Email sending failed for notification ID: {}", notification.getId());
                    }
                } else {
                    log.warn("Cannot send email to user {}: email is missing for notification ID: {}", personId, notification.getId());
                }
            }, () -> {
                log.warn("Cannot send email to user {}: user not found for notification ID: {}", personId, notification.getId());
            });
        } else if (channel == NotificationChannel.PUSH) {
            log.info("Simulating PUSH notification to user {}: {} - {} (Actual PUSH sender not implemented)", personId, title, message);
            // TODO: Реализовать pushSender.send(userDeviceToken, title, message);
        }
    }
}