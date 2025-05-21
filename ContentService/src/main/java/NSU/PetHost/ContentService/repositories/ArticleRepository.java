package NSU.PetHost.ContentService.repositories;

import NSU.PetHost.ContentService.models.Articles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ArticleRepository extends JpaRepository<Articles, Long> {

    Articles getArticlesById(long id);

    Page<Articles> findAllByStatusIs(String statusType, Pageable pageable);

    Page<Articles> findAllByOwnerIDAndStatusIs(long ownerID, String status, Pageable pageable);

    Page<Articles> findAllByCreatedAtIsAfterAndStatusIs(LocalDate date, String statusType, Pageable pageable);

    Page<Articles> findAllByTitleContainingAndStatusIs(String title, String statusType, Pageable pageable);
}
