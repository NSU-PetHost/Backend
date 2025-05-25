package NSU.PetHost.NotificationService.api.controller;

import NSU.PetHost.NotificationService.api.dto.NotificationScheduleRequest;
import NSU.PetHost.NotificationService.api.dto.NotificationScheduleResponse;
import NSU.PetHost.NotificationService.core.service.NotificationScheduleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification/schedule")
public class NotificationScheduleController {
    private final NotificationScheduleService scheduleManagementService;

    @Autowired
    public NotificationScheduleController(NotificationScheduleService scheduleManagementService) {
        this.scheduleManagementService = scheduleManagementService;
    }

    @PostMapping
    public ResponseEntity<NotificationScheduleResponse> createSchedule(
            @Valid @RequestBody NotificationScheduleRequest requestDto) {
        log.info("Request to create new schedule: {}", requestDto);
        // creatorUserId можно получать из SecurityContext
        Long creatorUserId = requestDto.getCreatedByUserId(); 
        
        if (requestDto.getCronExpression() == null || requestDto.getCronExpression().trim().isEmpty()) {
             throw new IllegalArgumentException("cronExpression is required for this type of schedule.");
        }
        NotificationScheduleResponse createdSchedule =
                scheduleManagementService.createSchedule(requestDto, creatorUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponse> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleManagementService.getScheduleById(id));
    }

    @GetMapping
    public ResponseEntity<List<NotificationScheduleResponse>> getAllSchedules() {
        return ResponseEntity.ok(scheduleManagementService.getAllSchedules());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody NotificationScheduleRequest requestDto) {
        Long updaterUserId = null; // Или из SecurityContext
        if (requestDto.getCronExpression() == null || requestDto.getCronExpression().trim().isEmpty()) {
             throw new IllegalArgumentException("cronExpression is required for this type of schedule.");
        }
        return ResponseEntity.ok(scheduleManagementService.updateSchedule(id, requestDto, updaterUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleManagementService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}