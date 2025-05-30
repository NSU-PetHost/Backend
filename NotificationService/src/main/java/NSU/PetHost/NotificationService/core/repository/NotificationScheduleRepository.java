package NSU.PetHost.NotificationService.core.repository;

import NSU.PetHost.NotificationService.core.model.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {
    List<NotificationSchedule> findByIsActiveTrue();
    List<NotificationSchedule> findByIsActiveTrueAndCronExpressionIsNull();
    List<NotificationSchedule> findByCreatedByUserIdAndIsActiveTrue(Long createdByUserId);

    List<NotificationSchedule> findByIsActiveTrueAndCronExpressionIsNotNull();

    List<NotificationSchedule> findAllByCreatedByUserId(Long createdByUserId);
}