package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.Authority;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.AuthorityRepository;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.util.exceptions.Authority.AuthorityNotFoundException;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonWithThisEmailExistsException;
import NSU.PetHost.AuthService.util.exceptions.Person.PersonWithThisNicknameExistsException;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final AuthorityRepository authorityRepository;
    private final PeopleRepository peopleRepository;

    public PersonService(AuthorityRepository authorityRepository, PeopleRepository peopleRepository) {
        this.authorityRepository = authorityRepository;
        this.peopleRepository = peopleRepository;
    }

    public void checkExistingPerson(Person person) {
       if (isExistingPersonFromEmail(person.getEmail())) {
           throw new PersonWithThisEmailExistsException("Person with " + person.getEmail() + " already exists");
       }
       if (isExistingPersonFromNickname(person.getNickname())) {
            throw new PersonWithThisNicknameExistsException("Person with " + person.getNickname() + " already exists");
       }
    }

    private boolean isExistingPersonFromNickname(String nickname) {
        return peopleRepository.findByNickname(nickname).isPresent();
    }

    private boolean isExistingPersonFromEmail(String email) {
        return peopleRepository.findByNickname(email).isPresent();
    }

    public Person addAuthorityToPerson(Person person, Authority authority) {
        person.addAuthority(authority);

        // Благодаря CascadeType.PERSIST и MERGE, связь будет сохранена.
        return peopleRepository.save(person);
    }

    public Person addAuthorityToPerson(int personID, int authorityID) {

        Person person = peopleRepository.findById(personID).orElseThrow(() -> new PersonNotFoundException("Person with id = " + personID + " not found"));
        Authority authority = authorityRepository.findById(authorityID).orElseThrow(() -> new AuthorityNotFoundException("Authority with id = " + authorityID + " not found"));

        person.addAuthority(authority);

        // Благодаря CascadeType.PERSIST и MERGE, связь будет сохранена.
        return peopleRepository.save(person);
    }

}
