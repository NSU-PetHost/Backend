package NSU.PetHost.NotificationService.api.controller;


import NSU.PetHost.NotificationService.api.dto.ErrorResponseDto;
import NSU.PetHost.NotificationService.api.dto.NotificationScheduleRequest;
import NSU.PetHost.NotificationService.api.dto.NotificationScheduleResponse;
import NSU.PetHost.NotificationService.core.error.ForbiddenAccessException;
import NSU.PetHost.NotificationService.core.error.InvalidRequestArgumentException;
import NSU.PetHost.NotificationService.core.model.enumerator.NotificationTargetType;
import NSU.PetHost.NotificationService.core.security.PersonDetails;
import NSU.PetHost.NotificationService.core.service.NotificationScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/notification/schedule")
@Tag(name = "Notification Schedules", description = "API for managing notification schedules")
@SecurityRequirement(name = "JWT")
public class NotificationScheduleController {
    private final NotificationScheduleService scheduleService;

    @Autowired
    public NotificationScheduleController(NotificationScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ADMIN"::equals);
    }

    @Operation(summary = "Create a new notification schedule",
            description = "Creates a new cron-based notification schedule. Any authenticated user can create a schedule for themselves or for all users if an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Schedule created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotificationScheduleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data (e.g., invalid cron, missing fields)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden, user tries to create a schedule for another user (non-admin) or invalid target type")
    })
    @PostMapping("/cron")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationScheduleResponse> createSchedule(
            @Valid @RequestBody NotificationScheduleRequest requestDto,
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) @AuthenticationPrincipal PersonDetails personDetails
    ) {
        // Логика авторизации:
        // Пользователь может создавать расписание для себя (SINGLE_USER и targetUserId == creatorUserId)
        // Администратор может создавать расписание для любого пользователя или для ALL_USERS
        Long creatorUserId = personDetails.getId()
        log.info("User ID {} creating schedule: {}", creatorUserId, requestDto);

        if (requestDto.getTargetType() == NotificationTargetType.SINGLE_USER) {
            if (requestDto.getTargetUserId() == null) {
                requestDto.setTargetUserId(creatorUserId);
            } else if (!requestDto.getTargetUserId().equals(creatorUserId) && !isAdmin(authentication)) {
                throw new ForbiddenAccessException("Users can only create schedules for themselves or admins for any user.");
            }
        } else if (requestDto.getTargetType() == NotificationTargetType.ALL_USERS) {
            requestDto.setTargetUserId(null);
            if (!isAdmin(authentication)) {
                throw new ForbiddenAccessException("Only admins can create schedules targeting all users.");
            }
        }
        if (requestDto.getTargetType() == NotificationTargetType.ALL_USERS) {
            requestDto.setTargetUserId(null);
        }

        NotificationScheduleResponse createdSchedule = scheduleService.createSchedule(requestDto, creatorUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    @Operation(summary = "Get a schedule by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotificationScheduleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Schedule not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden, user tries to access another user's schedule (non-admin)")
    })
    @GetMapping("/{id}")
    // Пользователь может смотреть свои расписания, админ - любые
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationScheduleResponse> getScheduleById(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) @AuthenticationPrincipal PersonDetails personDetails
    ) {
        Long requesterUserId = personDetails.getId();
        NotificationScheduleResponse scheduleDto = scheduleService.getScheduleById(id);

        if (!isAdmin(authentication) &&
                scheduleDto.getCreatedByUserId() != null &&
                !scheduleDto.getCreatedByUserId().equals(requesterUserId) &&
                !(scheduleDto.getTargetType() == NotificationTargetType.SINGLE_USER &&
                        scheduleDto.getTargetUserId() != null && scheduleDto.getTargetUserId().equals(requesterUserId))) {
            throw new ForbiddenAccessException("You can only view your own schedules or schedules targeted to you.");
        }
        return ResponseEntity.ok(scheduleDto);
    }

    @Operation(summary = "Get all schedules (for admins) or schedules created by/for the current user")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationScheduleResponse>> getAllSchedules(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) @AuthenticationPrincipal PersonDetails personDetails
    ) {
        Long requesterUserId = personDetails.getId();
        List<NotificationScheduleResponse> schedules;
        if (isAdmin(authentication)) {
            schedules = scheduleService.getAllSchedules();
        } else {
            schedules = scheduleService.getSchedulesForPerson(requesterUserId);
        }
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Update an existing schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden, user tries to update another user's schedule (non-admin)"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody NotificationScheduleRequest requestDto,
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) @AuthenticationPrincipal PersonDetails personDetails
    ) {
        Long updaterUserId = personDetails.getId();
        NotificationScheduleResponse existingSchedule = scheduleService.getScheduleById(id);

        if (!isAdmin(authentication) && existingSchedule.getCreatedByUserId() != null && !existingSchedule.getCreatedByUserId().equals(updaterUserId)) {
            throw new ForbiddenAccessException("You can only update your own schedules.");
        }

        if (requestDto.getTargetType() == NotificationTargetType.SINGLE_USER) {
            if (requestDto.getTargetUserId() == null) {
                requestDto.setTargetUserId(updaterUserId);
            } else if (!requestDto.getTargetUserId().equals(updaterUserId) && !isAdmin(authentication)) {
                throw new ForbiddenAccessException("Users can only create/update schedules for themselves or admins for any user.");
            }
        } else if (requestDto.getTargetType() == NotificationTargetType.ALL_USERS) {
            requestDto.setTargetUserId(null);
            if (!isAdmin(authentication)) {
                throw new ForbiddenAccessException("Only admins can create/update schedules targeting all users.");
            }
        }

        NotificationScheduleResponse updatedSchedule = scheduleService.updateSchedule(id, requestDto, updaterUserId);
        return ResponseEntity.ok(updatedSchedule);
    }

    @Operation(summary = "Delete a schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden, user tries to delete another user's schedule (non-admin)"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) @AuthenticationPrincipal PersonDetails personDetails
    )  {
        Long deleterUserId = personDetails.getId();
        NotificationScheduleResponse existingSchedule = scheduleService.getScheduleById(id);

        if (!isAdmin(authentication) &&
                existingSchedule.getCreatedByUserId() != null &&
                !existingSchedule.getCreatedByUserId().equals(deleterUserId)) {
            throw new ForbiddenAccessException("You can only delete your own schedules.");
        }

        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/notification/schedule")
//public class NotificationScheduleController {
//    private final NotificationScheduleService scheduleManagementService;
//
//    @Autowired
//    public NotificationScheduleController(NotificationScheduleService scheduleManagementService) {
//        this.scheduleManagementService = scheduleManagementService;
//    }
//
//    @PostMapping
//    public ResponseEntity<NotificationScheduleResponse> createSchedule(
//            @Valid @RequestBody NotificationScheduleRequest requestDto) {
//        log.info("Request to create new schedule: {}", requestDto);
//        // creatorUserId можно получать из SecurityContext
//        Long creatorUserId = requestDto.getCreatedByUserId(); 
//        
//        if (requestDto.getCronExpression() == null || requestDto.getCronExpression().trim().isEmpty()) {
//             throw new IllegalArgumentException("cronExpression is required for this type of schedule.");
//        }
//        NotificationScheduleResponse createdSchedule =
//                scheduleManagementService.createSchedule(requestDto, creatorUserId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
//    }
//    
//    @GetMapping("/{id}")
//    public ResponseEntity<NotificationScheduleResponse> getScheduleById(@PathVariable Long id) {
//        return ResponseEntity.ok(scheduleManagementService.getScheduleById(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<NotificationScheduleResponse>> getAllSchedules() {
//        return ResponseEntity.ok(scheduleManagementService.getAllSchedules());
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<NotificationScheduleResponse> updateSchedule(
//            @PathVariable Long id,
//            @Valid @RequestBody NotificationScheduleRequest requestDto) {
//        Long updaterUserId = null; // Или из SecurityContext
//        if (requestDto.getCronExpression() == null || requestDto.getCronExpression().trim().isEmpty()) {
//             throw new IllegalArgumentException("cronExpression is required for this type of schedule.");
//        }
//        return ResponseEntity.ok(scheduleManagementService.updateSchedule(id, requestDto, updaterUserId));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
//        scheduleManagementService.deleteSchedule(id);
//        return ResponseEntity.noContent().build();
//    }
//}