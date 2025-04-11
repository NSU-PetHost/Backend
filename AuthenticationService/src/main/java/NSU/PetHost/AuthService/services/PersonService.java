package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.responses.positive.CabinetResponse;
import NSU.PetHost.AuthService.exceptions.Authority.AuthorityNotFoundException;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.exceptions.Person.PersonWithThisEmailExistsException;
import NSU.PetHost.AuthService.exceptions.Person.PersonWithThisNicknameExistsException;
import NSU.PetHost.AuthService.models.Authority;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.AuthorityRepository;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PersonService {

    private final AuthorityRepository authorityRepository;
    private final PeopleRepository peopleRepository;

    public PersonService(AuthorityRepository authorityRepository, PeopleRepository peopleRepository) {
        this.authorityRepository = authorityRepository;
        this.peopleRepository = peopleRepository;
    }

    public CabinetResponse getCabinet(long personId) {

        Person person = peopleRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(Map.of("error", "Person not found")));

        return new CabinetResponse(
                person.getFirstName(),
                person.getSurname(),
                person.getPatronymic(),
                person.getEmail()
        );
    }

    public boolean isExistingPersonFromNickname(String nickname) {
        return peopleRepository.findByNickname(nickname).isPresent();
    }

    public boolean isExistingPersonFromEmail(String email) {
        return peopleRepository.findByEmail(email).isPresent();
    }

    public void setEmailVerified(String email) {
        Person person = peopleRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException(Map.of("error", "Person not found")));
        person.setEmailVerified(true);
        person.setUpdatedAt(LocalDateTime.now());
        peopleRepository.save(person);
    }

}
