package NSU.PetHost.NotificationService.core.repository;

import NSU.PetHost.NotificationService.core.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPersonIdAndChannelAndIsReadFalseOrderByCreatedAtDesc(Long personId, String channel);
    Page<Notification> findByPersonIdAndChannelOrderByCreatedAtDesc(Long personId, String channel, Pageable pageable);
    Optional<Notification> findByIdAndPersonIdAndChannel(Long id, Long personId, String channel);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.personId = :personId AND n.channel = :channel AND n.isRead = false")
    int markBatchAsReadByPersonIdAndChannel(@Param("personId") Long personId, @Param("channel") String channel);

    // общий метод для получения всех уведомлений пользователя (все каналы)
    Page<Notification> findByPersonIdOrderByCreatedAtDesc(Long personId, Pageable pageable);
    List<Notification> findByPersonIdAndIsReadFalseOrderByCreatedAtDesc(Long personId);
    List<Notification> findAll(Specification<Notification> spec, Sort sort);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.personId = :personId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("personId") Long personId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id = :id AND n.personId = :personId")
    int deleteByIdAndPersonId(@Param("id") Long id, @Param("personId") Long personId);

    Optional<Notification> getNotificationById(Long notificationId);
}