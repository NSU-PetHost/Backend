package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.repositories.AuthorityRepository;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.util.exceptions.Authority.AuthorityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public void registerPerson(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        enrichPerson(person);
        enrichPersonDefaultAuthorities(person);
        peopleRepository.save(person);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreated_who("spring-app AuthService");
    }

    private void enrichPersonDefaultAuthorities (Person person) {

        person.setAuthorities(new HashSet<>());

        person.addAuthority(authorityRepository.findByAuthorityName("SEE POSTS").orElseThrow(() -> new AuthorityNotFoundException("SEE POSTS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("SEE COMMENTS TO THE POSTS").orElseThrow(() -> new AuthorityNotFoundException("SEE COMMENTS TO THE POSTS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("SEE PERSONAL ACCOUNT").orElseThrow(() -> new AuthorityNotFoundException("SEE PERSONAL ACCOUNT DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("SEE PERSONAL STATS").orElseThrow(() -> new AuthorityNotFoundException("SEE PERSONAL STATS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("GET PERSONALIZED RECOMMENDS").orElseThrow(() -> new AuthorityNotFoundException("GET PERSONALIZED RECOMMENDS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("ADD PETS").orElseThrow(() -> new AuthorityNotFoundException("ADD PETS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("CREATE NOTES FOR THE YOUR PET").orElseThrow(() -> new AuthorityNotFoundException("CREATE NOTES FOR THE YOUR PET DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("COMMENT POSTS").orElseThrow(() -> new AuthorityNotFoundException("COMMENT POSTS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("WRITE POSTS").orElseThrow(() -> new AuthorityNotFoundException("WRITE POSTS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("GET NOTIFICATIONS").orElseThrow(() -> new AuthorityNotFoundException("GET NOTIFICATIONS DOES NOT EXIST")));
        person.addAuthority(authorityRepository.findByAuthorityName("ADD IN FAMILY").orElseThrow(() -> new AuthorityNotFoundException("ADD IN FAMILY DOES NOT EXIST")));

    }

}
