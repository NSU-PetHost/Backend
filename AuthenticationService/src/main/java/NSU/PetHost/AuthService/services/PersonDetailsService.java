package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByNickname(nickname);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User " + nickname + " not found");
        }

        return new PersonDetails(person.get());
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {

        Optional<Person> person = peopleRepository.findByEmail(email);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }

        return new PersonDetails(person.get());

    }

}
