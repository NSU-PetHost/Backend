package NSU.PetHost.NotificationService.api.controller;


import NSU.PetHost.NotificationService.api.dto.ErrorResponseDto;
import NSU.PetHost.NotificationService.api.dto.NotificationDto;
import NSU.PetHost.NotificationService.api.dto.NotificationRequest;
import NSU.PetHost.NotificationService.core.error.ResourceNotFoundException;
import NSU.PetHost.NotificationService.core.model.Notification;
import NSU.PetHost.NotificationService.core.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/v1/notification")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Уведомления (Notifications)", description = "API для управления уведомлениями пользователей")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Получить уведомления текущего пользователя",
            description = "Возвращает список уведомлений для аутентифицированного пользователя с возможностью фильтрации и сортировки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список уведомлений успешно получен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationDto>> getPersonNotifications(
            @AuthenticationPrincipal Jwt jwtPrincipal,
            @Parameter(description = "Фильтр по статусу прочтения (true/false). Если не указан, возвращаются все.", example = "false")
            @RequestParam(required = false) Boolean isRead,
            @Parameter(description = "Фильтр по имени категории уведомления.", example = "Новости")
            @RequestParam(required = false) String category,
            @Parameter(description = "Параметры сортировки. Формат: 'поле,направление' (например, 'createdAt,desc'). По умолчанию 'createdAt,asc'.", example = "createdAt,desc")
            @RequestParam(required = false, defaultValue = "createdAt,asc") String sort
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(jwtPrincipal);
        log.info("Request for notifications for authenticated user ID: {}, isRead: {}, category: {}, sort: {}",
                authenticatedUserId, isRead, category, sort);

        List<Notification> notifications = notificationService.getPersonNotifications(
                authenticatedUserId, isRead, category, sort
        );

        return ResponseEntity.ok(
                notifications.stream()
                        .map(this::convertToNotificationDto)
                        .collect(Collectors.toList())
        );
    }

    @Operation(summary = "Получить конкретное уведомление по ID",
            description = "Возвращает детали уведомления, если оно принадлежит аутентифицированному пользователю.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Детали уведомления",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (уведомление не принадлежит пользователю)"),
            @ApiResponse(responseCode = "404", description = "Уведомление не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping(value = "/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDto> getNotificationById(
            @Parameter(description = "ID уведомления", required = true, example = "1")
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Jwt jwtPrincipal
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(jwtPrincipal);
        log.info("User ID: {} requesting notification ID: {}", authenticatedUserId, notificationId);
        Notification notification = notificationService.getNotificationDetails(notificationId, authenticatedUserId);
        return ResponseEntity.ok(convertToNotificationDto(notification));
    }


    @Operation(summary = "Пометить уведомление как прочитанное",
            description = "Устанавливает флаг isRead=true для указанного уведомления, если оно принадлежит аутентифицированному пользователю.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление успешно помечено как прочитанное"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (уведомление не принадлежит пользователю)"),
            @ApiResponse(responseCode = "404", description = "Уведомление не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @AuthenticationPrincipal Jwt jwtPrincipal,
            @Parameter(description = "ID уведомления", required = true, example = "1") @PathVariable Long notificationId
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(jwtPrincipal);
        log.info("User ID: {} attempting to mark notification ID: {} as read", authenticatedUserId, notificationId);

        notificationService.markAsRead(notificationId, authenticatedUserId); // Сервис кинет исключение, если что-то не так
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Создать уведомление для указанного пользователя (административная функция)",
            description = "Позволяет администратору создать уведомление для конкретного пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Уведомление успешно создано",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры запроса",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    // @PreAuthorize("hasRole('ROLE_ADMIN')") // Пример защиты на основе роли
    @PostMapping(value = "/user/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDto> createNotificationForSpecificUser(
            @Parameter(description = "ID пользователя, для которого создается уведомление", required = true, example = "456")
            @PathVariable Long userId,
            @RequestBody(description = "Данные для создания уведомления", required = true,
                    content = @Content(schema = @Schema(implementation = NotificationRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody NotificationRequest request) {
        log.info("Admin request to create notification for user ID: {}. Request: {}", userId, request);
        Notification created = notificationService.createForPerson(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToNotificationDto(created));
    }

    @Operation(summary = "Создать уведомление для всех пользователей (административная функция)",
            description = "Массовая рассылка уведомлений всем зарегистрированным пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Уведомления успешно созданы и поставлены в очередь на отправку",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры запроса",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationDto>> createNotificationForAllUsers(
            @RequestBody(description = "Данные для создания массового уведомления (например, только title и message, канал может быть общим)",
                    required = true, content = @Content(schema = @Schema(implementation = NotificationRequest.class)))
            @Valid  @org.springframework.web.bind.annotation.RequestBody NotificationRequest request) {
        // Здесь может быть логика проверки прав администратора
        log.info("Admin request to create bulk notification: {}", request);
        List<Notification> createdNotifications = notificationService.createForAllPersons(request);
        List<NotificationDto> dtos = createdNotifications.stream()
                .map(this::convertToNotificationDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }

    @Operation(summary = "Обновить уведомление (административная или пользовательская функция)",
            description = "Позволяет обновить существующее уведомление. Пользователь может обновлять только свои уведомления, если не администратор.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление успешно обновлено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Уведомление не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping(value = "/{notificationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDto> updateNotification(
            @Parameter(description = "ID уведомления для обновления", required = true, example = "1")
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Jwt jwtPrincipal,
            @RequestBody(description = "Данные для обновления уведомления", required = true,
                    content = @Content(schema = @Schema(implementation = NotificationRequest.class)))
            @Valid  @org.springframework.web.bind.annotation.RequestBody NotificationRequest updateRequest
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(jwtPrincipal);
        log.info("User ID: {} attempting to update notification ID: {}. Request: {}", authenticatedUserId, notificationId, updateRequest);
        Notification updatedNotification = notificationService.updateNotification(
                notificationId,
                authenticatedUserId,
                updateRequest
        );
        return ResponseEntity.ok(convertToNotificationDto(updatedNotification));
    }

    @Operation(summary = "Удалить уведомление (пользовательская функция)",
            description = "Пользователь может удалить свое уведомление.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Уведомление успешно удалено"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (попытка удалить чужое уведомление)"),
            @ApiResponse(responseCode = "404", description = "Уведомление не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "ID уведомления для удаления", required = true, example = "1")
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Jwt jwtPrincipal) {
        Long authenticatedUserId = getAuthenticatedUserId(jwtPrincipal);
        log.info("User ID: {} attempting to delete notification ID: {}", authenticatedUserId, notificationId);
        notificationService.deleteNotificationForPerson(notificationId, authenticatedUserId); // Сервис проверяет принадлежность
        return ResponseEntity.noContent().build();
    }

    private NotificationDto convertToNotificationDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationDto dto = modelMapper.map(notification, NotificationDto.class);
        if (notification.getNotificationTemplate() != null) {
            dto.setNotificationTemplateId(notification.getNotificationTemplate().getId());
        }

        if (notification.getNotificationSchedule() != null) {
            dto.setNotificationScheduleId(notification.getNotificationSchedule().getId());
        }
        return dto;
    }

    private Long getAuthenticatedUserId(Jwt jwtPrincipal) {
        if (jwtPrincipal == null || jwtPrincipal.getSubject() == null) {
            throw new AuthenticationCredentialsNotFoundException("JWT principal or subject is missing.");
        }
        try {
            return Long.parseLong(jwtPrincipal.getSubject());
        } catch (NumberFormatException e) {
            log.error("Could not parse user ID from JWT subject: '{}'", jwtPrincipal.getSubject(), e);
            throw new IllegalArgumentException("Invalid user identifier format in token.");
        }
    }

}
//
//@RestController
//@RequestMapping("/api/v1/notification")
//@Slf4j
//@RequiredArgsConstructor
//@Tag(name = "Notifications", description = "Управление уведомлениями")
//public class NotificationController {
//    private final NotificationService notificationService;
//    private final ModelMapper modelMapper;
//
//    @Operation(
//            summary = "Создать уведомление для всех пользователей",
//            description = "Массовая рассылка уведомлений всем зарегистрированным пользователям",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "201",
//                            description = "Уведомления успешно созданы",
//                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
//                    ),
//                    @ApiResponse(
//                            responseCode = "400",
//                            description = "Невалидные параметры запроса",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//                    ),
//                    @ApiResponse(
//                            responseCode = "500",
//                            description = "Внутренняя ошибка сервера",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
//                    )
//            }
//    )
//    @PostMapping("/bulk")
//    public ResponseEntity<List<NotificationDto>> createNotificationForAllUsers(
//            @Valid @RequestBody NotificationRequest request) {
//
//        List<Notification> created = notificationService.createForAllUsers(request);
//        List<NotificationDto> dtos = created.stream()
//                .map(this::convertToNotificationDto)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
//    }
//
//    @Operation(summary = "Пометить уведомление как прочитанное",
//            description = "Устанавливает флаг isRead=true для указанного уведомления, если оно принадлежит аутентифицированному пользователю.")
//    @ApiResponse(responseCode = "200", description = "Уведомление успешно помечено как прочитанное")
//    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
//    @ApiResponse(responseCode = "404", description = "Уведомление не найдено или не принадлежит пользователю")
//    @PostMapping("/{notificationId}/read")
//    public ResponseEntity<Void> markNotificationAsRead(
//            @AuthenticationPrincipal Jwt jwtPrincipal,
//            @Parameter(description = "ID уведомления", required = true) @PathVariable Long notificationId
//    ) {
//        if (jwtPrincipal == null || jwtPrincipal.getSubject() == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        Long authenticatedUserId;
//        try {
//            authenticatedUserId = Long.parseLong(jwtPrincipal.getSubject());
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Invalid user identifier in token.");
//        }
//
//        log.info("User ID: {} attempting to mark notification ID: {} as read", authenticatedUserId, notificationId);
//        int updatedCount = notificationService.markAsRead(notificationId, authenticatedUserId);
//        if (updatedCount > 0) {
//            return ResponseEntity.ok().build();
//        } else {
//            // GlobalExceptionHandler обработает ResourceNotFoundException, если сервис его кидает
//            throw new ResourceNotFoundException(
//                    String.format("Notification with id %d not found for user %d, or already marked as read.", notificationId, authenticatedUserId)
//            );
//        }
//    }
//
//
//    @Operation(summary = "Получить уведомления текущего пользователя",
//            description = "Фильтрация и сортировка уведомлений по различным параметрам для аутентифицированного пользователя.")
//    @ApiResponse(responseCode = "200", description = "Список уведомлений",
//            content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class))))
//    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
//    @ApiResponse(responseCode = "403", description = "Доступ запрещен (например, если пытаться получить чужие уведомления, хотя здесь это не применимо)")
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Убрали /user/{userId} из пути
//    public ResponseEntity<List<NotificationDto>> getCurrentUserNotifications(
//            @AuthenticationPrincipal Jwt jwtPrincipal, // Получаем JWT токен
//            @Parameter(
//                    description = "Фильтр по статусу прочтения (true/false). Если не указан, возвращаются все.",
//                    example = "false"
//            ) @RequestParam(required = false) Boolean isRead,
//            @Parameter(
//                    description = "Фильтр по имени категории (например, 'SYSTEM', 'NEWS').",
//                    example = "SYSTEM"
//            ) @RequestParam(required = false) String category,
//            @Parameter(
//                    description = "Параметры сортировки. Формат: 'field1,direction1;field2,direction2'. Направления: asc/desc. По умолчанию 'createdAt,asc'.",
//                    example = "createdAt,desc;title,asc"
//            ) @RequestParam(required = false, defaultValue = "createdAt,asc") String sort
//    ) {
//        if (jwtPrincipal == null || jwtPrincipal.getSubject() == null) {
//            log.warn("Attempt to access notifications without a valid JWT principal or subject.");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Long authenticatedUserId;
//        try {
//            authenticatedUserId = Long.parseLong(jwtPrincipal.getSubject());
//        } catch (NumberFormatException e) {
//            log.error("Could not parse user ID from JWT subject: '{}'", jwtPrincipal.getSubject(), e);
//            throw new IllegalArgumentException("Invalid user identifier in token.");
//        }
//
//        log.info("Request for notifications for authenticated user ID: {}, isRead: {}, category: {}, sort: {}",
//                authenticatedUserId, isRead, category, sort);
//
//        List<Notification> notifications = notificationService.getUserNotifications(
//                authenticatedUserId, isRead, category, sort
//        );
//
//        return ResponseEntity.ok(
//                notifications.stream()
//                        .map(this::convertToNotificationDto)
//                        .collect(Collectors.toList())
//        );
//    }
//
//    @Operation(summary = "Создать уведомление для пользователя")
//    @ApiResponse(
//            responseCode = "201",
//            description = "Уведомление успешно создано",
//            content = @Content(schema = @Schema(implementation = NotificationDto.class))
//    )
//    @PostMapping("/user/{userId}")
//    public ResponseEntity<NotificationDto> createNotificationForUser(
//            @Parameter(
//                    description = "ID пользователя",
//                    example = "456",
//                    required = true
//            ) @PathVariable Long userId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "Данные для создания уведомления",
//                    required = true
//            ) @Valid @RequestBody NotificationRequest request) {
//
//        Notification created = notificationService.createForUser(userId, request);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(convertToNotificationDto(created));
//    }
//
//    @Operation(summary = "Удалить уведомление",
//            description = "Пользователь может удалить свое уведомление.")
//    @ApiResponse(responseCode = "204", description = "Уведомление успешно удалено")
//    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
//    @ApiResponse(responseCode = "404", description = "Уведомление не найдено")
//    @DeleteMapping("/{notificationId}")
//    public ResponseEntity<Void> deleteNotification(
//            @Parameter(description = "ID уведомления", required = true) @PathVariable Long notificationId,
//            @AuthenticationPrincipal Jwt principal) {
//        Long personId = getPersonIdFromPrincipal(principal);
//        notificationService.deleteNotification(notificationId, personId);
//        return ResponseEntity.noContent().build();
//    }
//
//    public Long getPersonIdFromPrincipal(Jwt principal) {
//        Long personId = principal.getClaim("personId");
//        if (personId == null) {
//            throw new AuthenticationCredentialsNotFoundException("Person ID not found in JWT");
//        }
//        return personId;
//    }
//
//
//    @Operation(summary = "Получить уведомление по ID")
//    @ApiResponse(responseCode = "200", description = "Детали уведомления",
//            content = @Content(schema = @Schema(implementation = NotificationDto.class)))
//    @ApiResponse(responseCode = "404", description = "Уведомление не найдено")
//    @GetMapping("/{notificationId}")
//    public ResponseEntity<NotificationDto> getNotificationById(
//            @Parameter(description = "ID уведомления", example = "123", required = true)
//            @PathVariable Long notificationId
//            // Возможно, @RequestParam Long personId для проверки, что это уведомление принадлежит пользователю
//    ) {
//        // Логика в notificationService.getNotificationById(notificationId, personId);
//        Notification notification = notificationService.getNotificationDetails(notificationId /*, personId */);
//        if (notification == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(convertToNotificationDto(notification));
//    }
//
//    @Operation(summary = "Обновить уведомление")
//    @ApiResponse(responseCode = "200", description = "Уведомление успешно обновлено",
//            content = @Content(schema = @Schema(implementation = NotificationDto.class)))
//    @ApiResponse(responseCode = "400", description = "Некорректные данные")
//    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
//    @ApiResponse(responseCode = "404", description = "Уведомление не найдено")
//    @PutMapping("/{notificationId}")
//    public ResponseEntity<NotificationDto> updateNotification(
//            @Parameter(description = "ID уведомления", required = true)
//            @PathVariable Long notificationId,
//            @AuthenticationPrincipal Jwt principal,
//            @Valid @RequestBody NotificationRequest updateRequest
//    ) {
//        Long personId = getPersonIdFromPrincipal(principal);
//        Notification updatedNotification = notificationService.updateNotification(
//                notificationId,
//                personId,
//                updateRequest
//        );
//        return ResponseEntity.ok(convertToNotificationDto(updatedNotification));
//    }
//    private Notification convertToNotification(NotificationDto clientDto) {
//        return modelMapper.map(clientDto, Notification.class);
//    }
//
//    private NotificationDto convertToNotificationDto(Notification notification) {
//        NotificationDto dto = modelMapper.map(notification, NotificationDto.class);
//        if (notification.getNotificationTemplate() != null) {
//            dto.setNotificationTemplateId(notification.getNotificationTemplate().getId());
//        }
//        return dto;
//    }
//}
