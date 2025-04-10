package NSU.PetHost.AuthService.repositories;

import NSU.PetHost.AuthService.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByAuthorityName(String authorityName);

    Optional<Authority> findById(int id);

}
