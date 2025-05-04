package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.responses.positive.CabinetResponse;
import NSU.PetHost.AuthService.dto.responses.positive.OkResponse;
import NSU.PetHost.AuthService.exceptions.Person.PersonNotFoundException;
import NSU.PetHost.AuthService.exceptions.Roles.RoleNotFoundException;
import NSU.PetHost.AuthService.exceptions.VerifyCode.VerifyCodeException;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.models.VerifyCode;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.repositories.RoleRepository;
import NSU.PetHost.AuthService.security.PersonDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final ModelMapper modelMapper;
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RedisService redisService;
    private final MailSenderService mailSenderService;

    public CabinetResponse getCabinet() {

        long idFromAuth = getCurrentId();

        Person person = peopleRepository.findById(idFromAuth).orElseThrow(() -> new PersonNotFoundException("Person not found"));

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
        Person person = peopleRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException("Person not found"));
        person.setEmailVerified(true);
        person.setUpdatedAt(OffsetDateTime.now());
        peopleRepository.save(person);
    }

    public long getCurrentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getId();
    }

    public Person getPersonByEmail(String email) {
        return peopleRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException("Person not found"));
    }

    public Person getPersonById(long id) {
        return peopleRepository.findById(id).orElseThrow(() -> new PersonNotFoundException("Person not found"));
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
        person.setCreatedAt(OffsetDateTime.now());
        person.setUpdatedAt(OffsetDateTime.now());
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

    public OkResponse resetPassword(@Email @NotNull String email) {

        Person person = getPersonByEmail(email);

        VerifyCode verifyCode = new VerifyCode(email, RegistrationService.generateVerifyCode(), false); //5 min за счёт TTL Redis

        redisService.addVerifyCode(verifyCode);

        mailSenderService.sendNotifyEmailResetPassword(verifyCode.getEmail(), verifyCode.getCode());

        return new OkResponse("Email sent");

    }

    public OkResponse changePassword(String email, String newPassword) {

        VerifyCode verifyCode = redisService.findVerifyCode(email);

        if (verifyCode == null) {
            throw new VerifyCodeException("Verification code expired");
        }

        Person person = getPersonByEmail(email);

        if (!verifyCode.isVerified()) throw new VerifyCodeException("Verification code not verified");

        person.setPassword(passwordEncoder.encode(newPassword));

        peopleRepository.save(person);

        redisService.deleteVerifyCode(email);

        return new OkResponse("Password changed successfully");

    }
}
