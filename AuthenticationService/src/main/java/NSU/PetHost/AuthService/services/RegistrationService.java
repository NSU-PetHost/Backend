package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.dto.requests.RegistrationDTO;
import NSU.PetHost.AuthService.dto.requests.VerifyAccountDTO;
import NSU.PetHost.AuthService.dto.responses.positive.RegistrationResponse;
import NSU.PetHost.AuthService.exceptions.Authority.AuthorityNotFoundException;
import NSU.PetHost.AuthService.exceptions.Person.*;
import NSU.PetHost.AuthService.models.Authority;
import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.models.VerifyCode;
import NSU.PetHost.AuthService.repositories.AuthorityRepository;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final Set<Authority> defaultAuthorities;
    private final Set<Authority> anonymousAuthorities;
    private final Set<Authority> adminAuthorities;
    private final ModelMapper modelMapper;
    private final PersonService personService;
    private final MailSenderService mailSenderService;
    private final RedisService redisService;

    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, ModelMapper modelMapper, PersonService personService, MailSenderService mailSenderService, RedisService redisService) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.modelMapper = modelMapper;
        this.personService = personService;
        this.mailSenderService = mailSenderService;
        this.redisService = redisService;
        this.defaultAuthorities = generateDefaultAuthorities();
        this.adminAuthorities = generateAdminAuthorities();
        this.anonymousAuthorities = generateAnonymousAuthorities();
    }

    public RegistrationResponse registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new PersonNotCreatedException(errors);
        }

        Person person = convertToPerson(registrationDTO);

        registerPerson(person);

        return new RegistrationResponse("Registration complete");
    }

    private Person convertToPerson(@Valid RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, Person.class);
    }

    public RegistrationResponse confirmEmail(@Valid @RequestBody String email, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new ConfirmEmailException(errors);
        }

        if (!personService.isExistingPersonFromEmail(email)) {
            throw new PersonNotFoundException(Map.of("email", "Person with " + email + " not exists"));
        }

        VerifyCode verifyCode = new VerifyCode(email, generateVerifyCode(), System.currentTimeMillis() + 5 * 60 * 1000);

        redisService.add(verifyCode);

        mailSenderService.sendEmail(verifyCode.getEmail(), verifyCode.getCode());

        return new RegistrationResponse("Email sent");
    }

    public RegistrationResponse confirmCode(@RequestBody @Valid VerifyAccountDTO verifyAccountDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

            throw new ConfirmEmailException(errors);
        }

        VerifyCode verifyCode = redisService.findVerifyCode(verifyAccountDTO.getEmail());

        if (verifyCode == null) {
             throw new ConfirmEmailException(Map.of("error", "Verification code not exists"));
        }

        if (verifyCode.getExpiryTime() < System.currentTimeMillis()) {
            throw new ConfirmEmailException(Map.of("error", "Verification code expired"));
        }

        if (verifyCode.getCode() == verifyAccountDTO.getVerifyCode()) {
            personService.setEmailVerified(verifyAccountDTO.getEmail());
            return new RegistrationResponse("Email verified");
        } else {
            throw new ConfirmEmailException(Map.of("error", "Verification code invalid"));
        }

    }

    private int generateVerifyCode() {
        return (int)(Math.random() * 900000) + 100000; // in range [100000;999999]
    }


    protected void registerPerson(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setEmailVerified(false);
        enrichPerson(person);
        if (person.getAuthorities() == null || person.getAuthorities().isEmpty()) person.setAuthorities(new HashSet<>());
        enrichPersonAnonymousAuthorities(person);
        enrichPersonDefaultAuthorities(person);
        peopleRepository.save(person);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreated_who("spring-app AuthService");
    }

    private void enrichPersonAnonymousAuthorities(Person person) {
        person.getAuthorities().addAll(anonymousAuthorities);
    }

    private void enrichPersonDefaultAuthorities(Person person) {
        person.getAuthorities().addAll(defaultAuthorities);
    }

    private void enrichPersonAdminAuthorities(Person person) {
        person.getAuthorities().addAll(adminAuthorities);
    }

    private Set<Authority> generateAnonymousAuthorities() {
        Set<Authority> authorities = new HashSet<>();

        authorities.add(authorityRepository.findByAuthorityName("SEE POSTS").orElseThrow(() -> new AuthorityNotFoundException("SEE POSTS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("SEE COMMENTS TO THE POSTS").orElseThrow(() -> new AuthorityNotFoundException("SEE COMMENTS TO THE POSTS DOES NOT EXIST")));

        return authorities;
    }

    private Set<Authority> generateDefaultAuthorities() {

        Set<Authority> authorities = new HashSet<>();

        authorities.add(authorityRepository.findByAuthorityName("SEE PERSONAL ACCOUNT").orElseThrow(() -> new AuthorityNotFoundException("SEE PERSONAL ACCOUNT DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("SEE PERSONAL STATS").orElseThrow(() -> new AuthorityNotFoundException("SEE PERSONAL STATS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("GET PERSONALIZED RECOMMENDS").orElseThrow(() -> new AuthorityNotFoundException("GET PERSONALIZED RECOMMENDS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("ADD PETS").orElseThrow(() -> new AuthorityNotFoundException("ADD PETS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("CREATE NOTES FOR THE YOUR PET").orElseThrow(() -> new AuthorityNotFoundException("CREATE NOTES FOR THE YOUR PET DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("COMMENT POSTS").orElseThrow(() -> new AuthorityNotFoundException("COMMENT POSTS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("WRITE POSTS").orElseThrow(() -> new AuthorityNotFoundException("WRITE POSTS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("GET NOTIFICATIONS").orElseThrow(() -> new AuthorityNotFoundException("GET NOTIFICATIONS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("ADD IN FAMILY").orElseThrow(() -> new AuthorityNotFoundException("ADD IN FAMILY DOES NOT EXIST")));

        return authorities;
    }

    private Set<Authority> generateAdminAuthorities() {

        Set<Authority> authorities = new HashSet<>();

        authorities.add(authorityRepository.findByAuthorityName("VERIFY AUTHENTICITY POSTS").orElseThrow(() -> new AuthorityNotFoundException("VERIFY AUTHENTICITY POSTS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("SEE LIST OF POSTS SUBMITTED FOR MODERATION").orElseThrow(() -> new AuthorityNotFoundException("SEE LIST OF POSTS SUBMITTED FOR MODERATION DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("PUBLISH POSTS").orElseThrow(() -> new AuthorityNotFoundException("PUBLISH POSTS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("SEE PERSONAL STATS").orElseThrow(() -> new AuthorityNotFoundException("SEE PERSONAL STATS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("REJECT PUBLISH POSTS").orElseThrow(() -> new AuthorityNotFoundException("REJECT PUBLISH POSTS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("ADD PETS").orElseThrow(() -> new AuthorityNotFoundException("ADD PETS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("CHANGE AUTHORITY OTHER USERS").orElseThrow(() -> new AuthorityNotFoundException("CHANGE AUTHORITY OTHER USERS DOES NOT EXIST")));
        authorities.add(authorityRepository.findByAuthorityName("BAN USERS").orElseThrow(() -> new AuthorityNotFoundException("BAN USERS DOES NOT EXIST")));

        return authorities;
    }
}
