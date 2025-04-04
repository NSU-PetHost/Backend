package NSU.PetHost.AuthService.repositories;

import NSU.PetHost.AuthService.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    Optional<Authority> findByAuthorityName(String authority_name);

    Optional<Authority> findById(int id);

}
