package NSU.PetHost.NotificationService.core.repository;

import NSU.PetHost.NotificationService.core.model.PersonSetting;
import NSU.PetHost.NotificationService.core.model.PersonSettingId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonSettingRepository extends JpaRepository<PersonSetting, PersonSettingId> {
    Optional<PersonSetting> findById_PersonIdAndId_CategoryId(Long personId, Integer categoryId);
}