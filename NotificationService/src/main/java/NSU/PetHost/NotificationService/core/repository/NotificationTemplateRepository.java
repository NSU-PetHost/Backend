package NSU.PetHost.NotificationService.core.repository;

import NSU.PetHost.NotificationService.core.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByName(String name);
}