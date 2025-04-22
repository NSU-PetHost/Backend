package NSU.PetHost.AuthService.repositories;

import NSU.PetHost.AuthService.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByNickname(String nickname);

    Optional<Person> findByEmail(String email);

    Optional<Person> findById(long id);

}
