package NSU.PetHost.NotificationService.core.repository;

import NSU.PetHost.NotificationService.core.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    Optional<EventCategory> findByName(String name);
}