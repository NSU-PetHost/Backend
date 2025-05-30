package NSU.PetHost.NotificationService.core.service;

import NSU.PetHost.NotificationService.api.dto.NotificationRequest;
import NSU.PetHost.NotificationService.core.model.EventCategory;
import NSU.PetHost.NotificationService.core.model.Notification;
import NSU.PetHost.NotificationService.core.model.NotificationSchedule;
import NSU.PetHost.NotificationService.core.model.NotificationTemplate;
import NSU.PetHost.NotificationService.core.error.InvalidRequestArgumentException;
import NSU.PetHost.NotificationService.core.error.ResourceNotFoundException;
import NSU.PetHost.NotificationService.core.repository.EventCategoryRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationScheduleRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationTemplateRepository;
import NSU.PetHost.NotificationService.core.service.client.PersonServiceClient;
import NSU.PetHost.proto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EventCategoryRepository categoryRepository;
    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationTemplateRepository templateRepository;
    private final PersonServiceClient personServiceClient; // предполагаем, что он есть для проверки пользователя
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "createdAt", "title", "isRead", "channel");


    @Transactional
    public List<Notification> createForAllPersons(NotificationRequest request) {
        log.info("Attempting to create notifications for all persons with request: {}", request);
        List<Long> allPersonIds = personServiceClient.getAllPersonIds();
        if (allPersonIds == null || allPersonIds.isEmpty()) {
            log.warn("No persons found to send bulk notification.");
            return List.of(); // возвращаем пустой список, если нет пользователей
        }

        NotificationTemplate template = resolveTemplateOptional(request.getNotificationTemplateId());
        NotificationSchedule schedule = resolveScheduleOptional(request.getNotificationScheduleId()); // Если нужно связывать

        List<Notification> notifications = allPersonIds.stream()
                .map(personId -> buildNotification(personId, request, template, schedule))
                .collect(Collectors.toList());

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        log.info("Successfully created {} notifications for all persons.", savedNotifications.size());
        return savedNotifications;
    }
    
    @Transactional
    public Notification createForPerson(Long personId, NotificationRequest request) {
        log.info("Attempting to create notification for person ID: {} with request: {}", personId, request);

        NotificationTemplate template = resolveTemplateOptional(request.getNotificationTemplateId());
        NotificationSchedule schedule = resolveScheduleOptional(request.getNotificationScheduleId());

        Notification notification = buildNotification(personId, request, template, schedule);
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Successfully created notification ID: {} for person ID: {}", savedNotification.getId(), personId);
        return savedNotification;
    }

    private NotificationTemplate resolveTemplateOptional(Long templateId) {
        if (templateId == null) return null;
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NotificationTemplate not found with id: " + templateId));
    }

    private NotificationSchedule resolveScheduleOptional(Long scheduleId) {
        if (scheduleId == null) return null;
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NotificationSchedule not found with id: " + scheduleId));
    }


    private Notification buildNotification(
            Long personId,
            NotificationRequest request,
            NotificationTemplate template,
            NotificationSchedule schedule
    ) {
        return Notification.builder()
                .personId(personId)
                .title(request.getTitle())
                .message(request.getMessage())
                .channel(request.getChannel())
                .notificationTemplate(template)
                .notificationSchedule(schedule)
                .isRead(false)
                .build();
    }

    @Transactional
    public int deleteNotification(Long notificationId, Long personId) {
        return notificationRepository.deleteByIdAndPersonId(notificationId, personId);
    }

    @Transactional
    public List<Notification> getPersonNotifications(
            Long personId,
            Boolean isRead,
            String categoryName,
            String sortParamsString
    ) {
        Specification<Notification> spec = (root, query, cb) ->
                cb.equal(root.get("personId"), personId);

        if (isRead != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isRead"), isRead));
        }

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            // проверка существования категории
            EventCategory category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + categoryName));
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("notificationTemplate").join("eventCategory").get("id"), category.getId())
            );
            log.debug("Filtering by category: {}", categoryName);
        }

        // парсинг и валидация параметра сортировки
        Sort sortOrder = parseAndValidateSortParameter(sortParamsString);
        log.debug("Applying sort order: {}", sortOrder);

        return notificationRepository.findAll(spec, sortOrder);
    }
    private Sort parseAndValidateSortParameter(String sortParamsString) {
        if (sortParamsString == null || sortParamsString.trim().isEmpty()) {
            return Sort.by(Sort.Order.asc("createdAt")); // Дефолтная сортировка
        }
        List<Sort.Order> orders = new ArrayList<>();
        // предполагаем, что параметры сортировки разделены точкой с запятой, например: "createdAt,asc;title,desc"
        String[] sortSegments = sortParamsString.split(";");

        for (String segment : sortSegments) {
            if (segment.trim().isEmpty()) continue;

            String[] parts = segment.split(",");
            if (parts.length != 2) {
                throw new InvalidRequestArgumentException("Invalid sort parameter format in segment: '" + segment + "'. Expected 'field,direction'.");
            }

            String field = parts[0].trim();
            String directionStr = parts[1].trim().toLowerCase();

            // проверка, что поле для сортировки допустимо
            if (!ALLOWED_SORT_FIELDS.contains(field)) {
                throw new InvalidRequestArgumentException("Invalid sort field: '" + field + "'. Allowed fields are: " + ALLOWED_SORT_FIELDS);
            }

            // проверка, что направление сортировки корректно
            Sort.Direction direction;
            if ("asc".equals(directionStr)) {
                direction = Sort.Direction.ASC;
            } else if ("desc".equals(directionStr)) {
                direction = Sort.Direction.DESC;
            } else {
                throw new InvalidRequestArgumentException("Invalid sort direction: '" + directionStr + "' in segment: '" + segment + "'. Allowed directions are 'asc' or 'desc'.");
            }
            orders.add(new Sort.Order(direction, field));
        }

        if (orders.isEmpty()) { // если после парсинга ничего не осталось (например, были только пустые сегменты)
            return Sort.by(Sort.Order.asc("createdAt"));
        }
        return Sort.by(orders);
    }


    private Sort parseSortParameter(String sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Order.desc("createdAt")); // значение по умолчанию
        }

        System.out.println("sortParams: " + sortParams);
        String[] parts = sortParams.split(";");

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : parts) {
            System.out.println("sortParam: " +sortParam);
            orders.add(parseOrder(sortParam));
        }


        return Sort.by(orders);
    }

    private Sort.Order parseOrder(String param) {
        System.out.println(param);
        String[] parts = param.split(",");
        System.out.println(Arrays.toString(parts));
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort parameter format. Expected: 'field,direction'");
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();


        return direction.equals("desc")
                ? Sort.Order.desc(field)
                : Sort.Order.asc(field);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long personId) {
        log.info("Attempting to mark notification ID: {} as read for person ID: {}", notificationId, personId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getPersonId().equals(personId)) {
            // проверка прав
            log.warn("Access denied: Person {} attempted to mark notification {} as read (owned by {}).",
                    personId, notificationId, notification.getPersonId());
            throw new AccessDeniedException("You can only mark your own notifications as read.");
        }
        if (notification.isRead()) {
            log.info("Notification ID: {} for person ID: {} is already marked as read.", notificationId, personId);
            return;
        }
        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("Notification ID: {} marked as read for person ID: {}", notificationId, personId);
    }

    @Transactional
    public Notification updateNotification(Long notificationId, Long personId, NotificationRequest request) {
        log.info("Attempting to update notification ID: {} for person ID: {} with request: {}",
                notificationId, personId, request);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getPersonId().equals(personId)) {
            log.warn("Access denied: Person {} attempted to update notification {} (owned by {}).",
                    personId, notificationId, notification.getPersonId());
            throw new AccessDeniedException("You can only update your own notifications.");
        }

        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());

        Notification updatedNotification = notificationRepository.save(notification);
        log.info("Successfully updated notification ID: {}", updatedNotification.getId());
        return updatedNotification;
    }


    @Transactional
    public void deleteNotificationForPerson(Long notificationId, Long personId) {
        log.info("Attempting to delete notification ID: {} for person ID: {}", notificationId, personId);
        int deletedCount = notificationRepository.deleteByIdAndPersonId(notificationId, personId);
        if (deletedCount == 0) {
            if (!notificationRepository.existsById(notificationId)) {
                throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
            } else {
                log.warn("Access denied: Person {} attempted to delete notification {} (not owned or already deleted).",
                        personId, notificationId);
                throw new AccessDeniedException("You can only delete your own notifications or notification not found.");
            }
        }
        log.info("Successfully deleted notification ID: {} for person ID: {}", notificationId, personId);
    }

    @Transactional
    public Notification getNotificationDetails(Long notificationId, Long personId) {
        log.debug("Fetching details for notification ID: {} for user ID: {}", notificationId, personId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getPersonId().equals(personId)) {
            // здесь можно добавить проверку на роль администратора, если админ может смотреть чужие
            log.warn("Access denied: User {} attempted to access notification {} owned by {}",
                    personId, notificationId, notification.getPersonId());
            throw new AccessDeniedException("You do not have permission to view this notification.");
        }
        return notification;
    }

    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }


//    public int markAsRead(Long notificationId, Long personId) {
//        return notificationRepository.markAsRead(notificationId, personId);
//    }
//
//    public Notification getNotificationDetails(Long notificationId) {
//        return notificationRepository.getNotificationById(notificationId)
//                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + notificationId));
//    }
//
//    public Notification updateNotification(
//            Long notificationId,
//            Long personId,
//            NotificationRequest request
//    ) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
//
//        PersonResponse response = personServiceClient.getPersonById(personId)
//                .orElseThrow(() -> new EntityNotFoundException("Person not found"));
//
//        notification.setTitle(request.getTitle());
//        notification.setMessage(request.getMessage());
//        notification.setChannel(request.getChannel());
//
//        NotificationTemplate notificationTemplate = templateRepository.findById(request.getNotificationTemplateId())
//                .orElseThrow(() -> new EntityNotFoundException("Notification template not found"));
//
//        NotificationSchedule notificationSchedule = scheduleRepository.findById(request.getNotificationScheduleId())
//                .orElseThrow(() -> new EntityNotFoundException("Notification schedule not found"));
//
//        notification.setNotificationTemplate(notificationTemplate);
//        notification.setNotificationSchedule(notificationSchedule);
//
//        return notificationRepository.save(notification);
//    }
}