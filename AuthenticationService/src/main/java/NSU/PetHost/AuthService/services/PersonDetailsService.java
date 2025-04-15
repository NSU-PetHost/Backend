package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    private PersonDetails loadUserByNickname(String nickname) throws UsernameNotFoundException {
        return new PersonDetails(peopleRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException("User " + nickname + " not found")));

    }

    public PersonDetails loadUserByEmail(String email) throws UsernameNotFoundException {

        return new PersonDetails(peopleRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found")));

    }

    @Override
    public PersonDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        return loadUserByNickname(nickname);
    }
}
