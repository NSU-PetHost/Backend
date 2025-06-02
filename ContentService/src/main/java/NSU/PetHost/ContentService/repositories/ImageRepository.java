package NSU.PetHost.ContentService.repositories;

import NSU.PetHost.ContentService.models.Images;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Images, Long> {


    List<Images> getImagesByOwnerID(long id);
}
