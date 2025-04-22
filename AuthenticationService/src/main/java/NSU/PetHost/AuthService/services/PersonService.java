package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.responses.positive.CabinetResponse;
import NSU.PetHost.AuthService.exceptions.Person.CustomAccessDeniedException;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.exceptions.Roles.RoleNotFoundException;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.repositories.RoleRepository;
import NSU.PetHost.AuthService.security.PersonDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final ModelMapper modelMapper;
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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

    public long getCurrentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getId();
    }

    public Person convertToPerson(@Valid RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, Person.class);
    }

    protected void registerPerson(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setEmailVerified(false);
        enrichPerson(person);
        enrichPersonUserRole(person);
        peopleRepository.save(person);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreated_who("spring-app AuthService");
    }

    private void enrichPersonAnonymousRole(Person person) {
        person.setRole(roleRepository.findByRoleName("ANONYMOUS").orElseThrow(() -> new RoleNotFoundException("Anonymous role not found")));
    }

    private void enrichPersonUserRole(Person person) {
        person.setRole(roleRepository.findByRoleName("USER").orElseThrow(() -> new RoleNotFoundException("User role not found")));
    }

    private void enrichPersonAdminRole(Person person) {
        person.setRole(roleRepository.findByRoleName("ADMIN").orElseThrow(() -> new RoleNotFoundException("Admin role not found")));
    }

}
