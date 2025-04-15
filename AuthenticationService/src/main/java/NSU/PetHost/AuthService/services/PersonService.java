package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.responses.positive.CabinetResponse;
import NSU.PetHost.AuthService.exceptions.Person.ConfirmEmailException;
import NSU.PetHost.AuthService.exceptions.Person.CustomAccessDeniedException;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.handlers.CustomAccessDeniedHandler;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.AuthorityRepository;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.security.JWTUtil;
import NSU.PetHost.AuthService.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final JWTUtil jwtUtil;
    private final PeopleRepository peopleRepository;

    public CabinetResponse getCabinet(long personId) {

        long idFromAuth = getCurrentId();

        if (personId != idFromAuth) throw new CustomAccessDeniedException("Access denied");

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

    private long getCurrentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getId();
    }

}
