package NSU.PetHost.NotificationService.core.scheduler;

import NSU.PetHost.NotificationService.core.model.NotificationSchedule;
import NSU.PetHost.NotificationService.core.model.NotificationTemplate;
import NSU.PetHost.NotificationService.core.model.PersonSetting;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationChannel;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.repository.NotificationScheduleRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationTemplateRepository;
import NSU.PetHost.NotificationService.core.repository.PersonSettingRepository;
import NSU.PetHost.NotificationService.core.service.NotificationCreationService;
import NSU.PetHost.NotificationService.core.service.client.PersonServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
public class DatabaseDrivenScheduler {
    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationCreationService notificationCreationService;
    private final PersonServiceClient userServiceClient;
    private final PersonSettingRepository personSettingRepository;

    @Autowired
    public DatabaseDrivenScheduler(
            NotificationScheduleRepository scheduleRepository,
            NotificationTemplateRepository templateRepository,
            NotificationCreationService notificationCreationService,
            PersonServiceClient userServiceClient,
            PersonSettingRepository personSettingRepository) {
        this.scheduleRepository = scheduleRepository;
        this.templateRepository = templateRepository;
        this.notificationCreationService = notificationCreationService;
        this.userServiceClient = userServiceClient;
        this.personSettingRepository = personSettingRepository;
    }


    @Scheduled(fixedRateString = "${app.scheduler.dbDriven.checkRateMillis:5000}")
    @Transactional
    public void processDatabaseSchedules() {
        OffsetDateTime nowSystem = OffsetDateTime.now();
        log.info("--- [SCHEDULER RUN @ {}] Processing database-driven schedules...", nowSystem);

        List<NotificationSchedule> activeSchedules =
                scheduleRepository.findByIsActiveTrueAndCronExpressionIsNotNull();

        if (activeSchedules.isEmpty()) {
            log.info("--- [SCHEDULER RUN] No active CRON schedules found.");
            return;
        }
        log.info("--- [SCHEDULER RUN] Found {} active schedules. Current system time: {}", activeSchedules.size(), nowSystem);

        for (NotificationSchedule scheduleFromList : activeSchedules) {
            NotificationSchedule currentScheduleState = scheduleRepository.findById(scheduleFromList.getId()).orElse(null);
            if (currentScheduleState == null || !currentScheduleState.isActive()) {
                log.info("--- --- Schedule ID: {} no longer exists or is inactive. Skipping.", scheduleFromList.getId());
                continue;
            }
            log.info("--- --- Evaluating Schedule ID: {}, Name: '{}'", currentScheduleState.getId(), currentScheduleState.getName());
            try {
                if (shouldTriggerAndUpdate(currentScheduleState, nowSystem)) {
                    log.warn(">>> TRIGGERING Schedule ID: {}, Name: '{}' for calculated time: {}",
                            currentScheduleState.getId(), currentScheduleState.getName(), currentScheduleState.getLastTriggeredAtByScheduler());
                    triggerNotificationForDbSchedule(currentScheduleState);
                }
            } catch (Exception e) {
                log.error("--- --- Error processing Schedule ID {}: {}", currentScheduleState.getId(), e.getMessage(), e);
            }
        }
        log.info("--- [SCHEDULER RUN] Finished processing schedules.");
    }

    /**
     * Определяет, должно ли расписание сработать, и обновляет его lastTriggeredAtByScheduler.
     * Этот метод находит самое последнее время, когда расписание *должно было* сработать
     * до или в момент `nowSystem`, и если это время новее, чем сохраненное
     * `lastTriggeredAtByScheduler`, то обновляет его и возвращает true.
     *
     * @param schedule  Актуальное состояние расписания из БД.
     * @param nowSystem Текущее системное время.
     * @return true, если уведомление должно быть отправлено, иначе false.
     */
    private boolean shouldTriggerAndUpdate(NotificationSchedule schedule, OffsetDateTime nowSystem) {
        if (schedule.getCronExpression() == null || schedule.getCronExpression().trim().isEmpty()) {
            log.warn("[SCHED ID: {}] Cron expression is missing. Skipping.", schedule.getId());
            return false;
        }

        CronExpression cron;
        try {
            cron = CronExpression.parse(schedule.getCronExpression());
        } catch (IllegalArgumentException e) {
            log.error("[SCHED ID: {}] Invalid cron expression: '{}'. Deactivating. Error: {}",
                    schedule.getId(), schedule.getCronExpression(), e.getMessage());
            schedule.setActive(false);
            scheduleRepository.save(schedule);
            return false;
        }

        ZoneId scheduleTimeZone = ZoneId.of(schedule.getTimezone());
        LocalDateTime nowInScheduleZone = nowSystem.atZoneSameInstant(scheduleTimeZone).toLocalDateTime();

        log.debug("[SCHED ID: {}] Evaluating schedule. Now in schedule zone ({}): {}. System time: {}",
                schedule.getId(), schedule.getTimezone(), nowInScheduleZone, nowSystem);

        // проверка временных ограничений расписания
        if (schedule.getStartDatetime() != null && nowSystem.isBefore(schedule.getStartDatetime())) {
            log.debug("[SCHED ID: {}] Schedule not started yet. StartDatetime: {}", schedule.getId(), schedule.getStartDatetime());
            return false;
        }

        if (schedule.getEndDatetime() != null && nowSystem.isAfter(schedule.getEndDatetime())) {
            log.info("[SCHED ID: {}] Schedule expired. EndDatetime: {}. Deactivating.", schedule.getId(), schedule.getEndDatetime());
            schedule.setActive(false);
            scheduleRepository.save(schedule);
            return false;
        }

        // время, от которого будем считать следующий запуск.
        // это либо время последнего успешного запуска, либо (для первого раза)
        // время начала расписания или его создания, минус небольшой интервал, чтобы "поймать" самый первый запуск.
        LocalDateTime baseTimeForCronCalculation;
        LocalDateTime originalLastTriggeredTimeInZone = null; // Для сравнения, было ли реальное обновление

        if (schedule.getLastTriggeredAtByScheduler() != null) {
            originalLastTriggeredTimeInZone = schedule.getLastTriggeredAtByScheduler()
                    .atZoneSameInstant(scheduleTimeZone)
                    .toLocalDateTime();
            baseTimeForCronCalculation = originalLastTriggeredTimeInZone;
            log.debug("[SCHED ID: {}] Last triggered at (DB): {}. Base for cron.next(): {}",
                    schedule.getId(), originalLastTriggeredTimeInZone, baseTimeForCronCalculation);
        } else {
            // первый запуск. Определяем "эффективное" время старта.
            OffsetDateTime effectiveStart = schedule.getCreatedAt(); // по умолчанию - время создания
            if (schedule.getStartDatetime() != null && schedule.getStartDatetime().isAfter(effectiveStart)) {
                effectiveStart = schedule.getStartDatetime(); // Если есть startDatetime и он позже - используем его
            }
            // чтобы cron.next() правильно определил запуск В effectiveStart,
            // нужно отталкиваться от времени чуть РАНЬШЕ.
            baseTimeForCronCalculation = effectiveStart.atZoneSameInstant(scheduleTimeZone).toLocalDateTime().minusNanos(1);
            log.debug("[SCHED ID: {}] First run. Effective start for cron: {}. Base for cron.next(): {}",
                    schedule.getId(), effectiveStart, baseTimeForCronCalculation);
        }

        LocalDateTime latestFireTimeToConsider = null;
        LocalDateTime nextPotentialFireTime = cron.next(baseTimeForCronCalculation);

        // ищем все пропущенные/текущие моменты запуска
        // latestFireTimeToConsider будет содержать САМЫЙ ПОЗДНИЙ из них
        while (nextPotentialFireTime != null && !nextPotentialFireTime.isAfter(nowInScheduleZone)) {
            log.debug("[SCHED ID: {}] Found potential fire time: {} (based on previous: {}). Now in zone: {}",
                    schedule.getId(), nextPotentialFireTime, baseTimeForCronCalculation, nowInScheduleZone);
            latestFireTimeToConsider = nextPotentialFireTime;
            baseTimeForCronCalculation = nextPotentialFireTime; // для следующей итерации отталкиваемся от найденного
            nextPotentialFireTime = cron.next(baseTimeForCronCalculation);
        }

        if (latestFireTimeToConsider != null) {
            // если мы нашли подходящее время для запуска (оно не null),
            // и это время действительно НОВЕЕ, чем то, что уже записано (или это первый запуск)
            if (originalLastTriggeredTimeInZone == null || latestFireTimeToConsider.isAfter(originalLastTriggeredTimeInZone)) {
                log.info("[SCHED ID: {}] Cron match! New fire time: {}. Previous recorded: {}",
                        schedule.getId(), latestFireTimeToConsider, originalLastTriggeredTimeInZone);

                schedule.setLastTriggeredAtByScheduler(
                        latestFireTimeToConsider.atZone(scheduleTimeZone).toOffsetDateTime()
                );
                scheduleRepository.save(schedule); // сохраняем обновленное время
                return true;
            } else {
                log.debug("[SCHED ID: {}] Calculated fire time ({}) is not after previously recorded ({}). No trigger.",
                        schedule.getId(), latestFireTimeToConsider, originalLastTriggeredTimeInZone);
            }
        } else {
            log.trace("[SCHED ID: {}] No fire time found <= nowInScheduleZone ({}). Next potential after {}: {}",
                    schedule.getId(), nowInScheduleZone, baseTimeForCronCalculation, nextPotentialFireTime);
        }

        return false;
    }

    private void triggerNotificationForDbSchedule(NotificationSchedule schedule) {
        Optional<NotificationTemplate> templateOpt = templateRepository.findById(schedule.getNotificationTemplate().getId());
        if (templateOpt.isEmpty()) {
            log.error("Template not found for schedule ID {}. Skipping trigger.", schedule.getId());
            return;
        }
        NotificationTemplate template = templateOpt.get();
        List<Long> targetPersonIds = determineTargetPersonIdsFromSchedule(schedule);

        if (targetPersonIds.isEmpty()) {
            log.info("No target persons found for schedule ID {}. Skipping trigger.", schedule.getId());
            return;
        }

        log.info("Triggering notifications for schedule ID {} to {} persons.", schedule.getId(), targetPersonIds.size());
        for (Long personId : targetPersonIds) {
            processAndSendToPerson(personId, schedule, template);
        }
    }

    private List<Long> determineTargetPersonIdsFromSchedule(NotificationSchedule schedule) {
        if (schedule.getTargetType() == NotificationTargetType.SINGLE_USER) {
            if (schedule.getTargetUserId() != null) {
                return Collections.singletonList(schedule.getTargetUserId());
            } else {
                log.warn("Schedule ID {} is SINGLE_USER but targetUserId is null.", schedule.getId());
                return Collections.emptyList();
            }
        } else if (schedule.getTargetType() == NotificationTargetType.ALL_USERS) {
            List<Long> allPersonIds = userServiceClient.getAllPersonIds();
            log.debug("Targeting ALL_USERS for schedule ID {}. Found {} users.", schedule.getId(), allPersonIds.size());
            return allPersonIds;
        }
        log.warn("Unknown target type or configuration for schedule ID {}.", schedule.getId());
        return Collections.emptyList();
    }

    private void processAndSendToPerson(Long personId, NotificationSchedule schedule, NotificationTemplate template) {
        Set<String> channelsToSend = new HashSet<>();
        // приоритет: каналы, указанные прямо в расписании
        if (schedule.getTargetChannels() != null && !schedule.getTargetChannels().isEmpty()) {
            channelsToSend.addAll(schedule.getTargetChannels());
            log.debug("[SCHED ID: {}, Person ID: {}] Using channels from schedule: {}", schedule.getId(), personId, channelsToSend);
        } else {
            // если в расписании каналы не указаны, смотрим персональные настройки
            Optional<PersonSetting> personSettingOpt = personSettingRepository.findById_PersonIdAndId_CategoryId(
                    personId, template.getEventCategory().getId());
            if (personSettingOpt.isPresent() && personSettingOpt.get().getChannels() != null && !personSettingOpt.get().getChannels().isEmpty()) {
                channelsToSend.addAll(personSettingOpt.get().getChannels());
                log.debug("[SCHED ID: {}, Person ID: {}] Using channels from person settings: {}", schedule.getId(), personId, channelsToSend);
            }
            // если и там нет, смотрим каналы по умолчанию из шаблона
            else if (template.getDefaultChannels() != null && !template.getDefaultChannels().isEmpty()) {
                channelsToSend.addAll(template.getDefaultChannels());
                log.debug("[SCHED ID: {}, Person ID: {}] Using default channels from template: {}", schedule.getId(), personId, channelsToSend);
            }
        }

        if (channelsToSend.isEmpty()) {
            log.info("No channels configured to send notification for schedule ID {}, person ID {}. Skipping.",
                    schedule.getId(), personId);
            return;
        }

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("personId", String.valueOf(personId));

        userServiceClient.getPersonById(personId).ifPresent(user -> {
            templateParams.put("username", user.getUsername());
            templateParams.put("firstName", user.getFirstName());
            templateParams.put("lastName", user.getLastName());
            templateParams.put("email", user.getEmail());
        });

        String renderedSubject = renderTemplate(template.getSubjectTemplate(), templateParams);
        String renderedBody = renderTemplate(template.getBodyTemplate(), templateParams);

        for (String channelStr : channelsToSend) {
            try {
                NotificationChannel channel = NotificationChannel.valueOf(channelStr.trim().toUpperCase());
                log.info("Sending notification for schedule ID {}, person ID {} via channel {}",
                        schedule.getId(), personId, channel);
                notificationCreationService.createAndSendNotification(
                        personId,
                        renderedSubject,
                        renderedBody,
                        channel,
                        template.getId(),
                        schedule.getId());
            } catch (IllegalArgumentException e) {
                log.error("Invalid channel string '{}' for schedule ID {}, person ID {}. Error: {}",
                        channelStr, schedule.getId(), personId, e.getMessage());
            } catch (Exception e) {
                log.error("Error sending notification for schedule ID {}, person ID {} via channel {}: {}",
                        schedule.getId(), personId, channelStr, e.getMessage(), e);
            }
        }
    }

    private String renderTemplate(String templateStr, Map<String, Object> params) {
        if (templateStr == null || templateStr.isEmpty()) {
            return "";
        }
        String result = templateStr;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
