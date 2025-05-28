package NSU.PetHost.NotificationService.core.service;

import NSU.PetHost.NotificationService.api.dto.NotificationScheduleRequest;
import NSU.PetHost.NotificationService.api.dto.NotificationScheduleResponse;
import NSU.PetHost.NotificationService.api.dto.NotificationTemplateDTO;
import NSU.PetHost.NotificationService.core.model.NotificationSchedule;
import NSU.PetHost.NotificationService.core.model.NotificationTemplate;
import NSU.PetHost.NotificationService.core.error.InvalidRequestArgumentException;
import NSU.PetHost.NotificationService.core.error.ResourceNotFoundException;
import NSU.PetHost.NotificationService.core.repository.NotificationScheduleRepository;
import NSU.PetHost.NotificationService.core.repository.NotificationTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression; // Для валидации
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationScheduleService {
    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationTemplateRepository templateRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationScheduleService(NotificationScheduleRepository scheduleRepository,
                                       NotificationTemplateRepository templateRepository,
                                       ModelMapper modelMapper) {
        this.scheduleRepository = scheduleRepository;
        this.templateRepository = templateRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public NotificationScheduleResponse createSchedule(NotificationScheduleRequest requestDto, Long creatorUserId) {
        if (requestDto.getCronExpression() == null || requestDto.getCronExpression().trim().isEmpty()) {
            throw new InvalidRequestArgumentException("cronExpression is required.");
        }
        if (!CronExpression.isValidExpression(requestDto.getCronExpression())) {
            throw new InvalidRequestArgumentException("Invalid cron expression format: " + requestDto.getCronExpression());
        }
        if (requestDto.getTimezone() == null || requestDto.getTimezone().trim().isEmpty()) {
            throw new InvalidRequestArgumentException("Timezone is required for cron schedules.");
        }
        try {
            ZoneId.of(requestDto.getTimezone());
        } catch (Exception e) {
            throw new InvalidRequestArgumentException("Invalid timezone ID: " + requestDto.getTimezone());
        }

        NotificationTemplate template = templateRepository.findById(requestDto.getNotificationTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + requestDto.getNotificationTemplateId()));

        NotificationSchedule scheduleEntity = modelMapper.map(requestDto, NotificationSchedule.class);
        scheduleEntity.setNotificationTemplate(template);
        if (creatorUserId != null) {
             scheduleEntity.setCreatedByUserId(creatorUserId);
        } else {
             scheduleEntity.setCreatedByUserId(requestDto.getCreatedByUserId());
        }
        scheduleEntity.setActive(requestDto.isActive());
        // lastTriggeredAtByScheduler будет null при создании

        NotificationSchedule savedSchedule = scheduleRepository.save(scheduleEntity);
        log.info("Created NotificationSchedule (for @Scheduled) ID: {}", savedSchedule.getId());
        return convertToDto(savedSchedule);
    }

    @Transactional(readOnly = true)
    public NotificationScheduleResponse getScheduleById(Long id) {
        NotificationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationSchedule not found with id: " + id));
        return convertToDto(schedule);
    }

    @Transactional(readOnly = true)
    public List<NotificationScheduleResponse> getAllSchedules() {
        // Теперь возвращаем все, так как все они управляются @Scheduled
        return scheduleRepository.findAll().stream() 
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationScheduleResponse updateSchedule(Long id, NotificationScheduleRequest requestDto, Long updaterUserId) {
        // Валидация cron и timezone, если они меняются
        if (requestDto.getCronExpression() != null && !requestDto.getCronExpression().trim().isEmpty() &&
            !CronExpression.isValidExpression(requestDto.getCronExpression())) {
            throw new InvalidRequestArgumentException("Invalid cron expression format: " + requestDto.getCronExpression());
        }
         if (requestDto.getTimezone() != null && !requestDto.getTimezone().trim().isEmpty()) {
            try { ZoneId.of(requestDto.getTimezone()); } 
            catch (Exception e) { throw new InvalidRequestArgumentException("Invalid timezone ID: " + requestDto.getTimezone()); }
        }


        NotificationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationSchedule not found with id: " + id));

        NotificationTemplate template = templateRepository.findById(requestDto.getNotificationTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + requestDto.getNotificationTemplateId()));

        modelMapper.map(requestDto, schedule);
        schedule.setNotificationTemplate(template);

        NotificationSchedule updatedSchedule = scheduleRepository.save(schedule);
        log.info("Updated NotificationSchedule (for @Scheduled) ID: {}", updatedSchedule.getId());
        return convertToDto(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("NotificationSchedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
        log.info("Deleted NotificationSchedule (for @Scheduled) ID: {}", id);
    }

    private NotificationScheduleResponse convertToDto(NotificationSchedule schedule) {
        NotificationScheduleResponse scheduleDto = modelMapper.map(schedule, NotificationScheduleResponse.class);

        if (schedule.getNotificationTemplate() != null) {
            NotificationTemplateDTO templateDto = modelMapper.map(schedule.getNotificationTemplate(), NotificationTemplateDTO.class);
            if (schedule.getNotificationTemplate().getEventCategory() != null) {
                templateDto.setEventCategoryId(schedule.getNotificationTemplate().getEventCategory().getId());
            }
            scheduleDto.setNotificationTemplate(templateDto);
        }

        scheduleDto.setLastTriggeredAtByScheduler(schedule.getLastTriggeredAtByScheduler());

        return scheduleDto;
    }

}