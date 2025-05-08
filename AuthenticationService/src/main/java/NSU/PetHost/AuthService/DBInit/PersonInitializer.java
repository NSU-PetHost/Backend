package NSU.PetHost.AuthService.DBInit;

import NSU.PetHost.AuthService.models.Person;
import NSU.PetHost.AuthService.models.Role;
import NSU.PetHost.AuthService.repositories.PeopleRepository;
import NSU.PetHost.AuthService.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class PersonInitializer {

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random();

    public void run(String... args) {
        if (peopleRepository.count() > 0) return;

        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            System.out.println("No roles found. Cannot initialize people.");
            return;
        }

        List<Person> personList = new ArrayList<>();

        IntStream.range(1, 101).forEach(i -> {
            Person person = new Person();
            person.setFirstName("Имя" + i);
            person.setSurname("Фамилия" + i);
            person.setSurname("Отчество" + i);
            person.setNickname("nickname" + i);
            person.setEmail("user" + i + "@example.com");
            person.setPassword(passwordEncoder.encode("password" + i));
            person.setEmailVerified(random.nextBoolean());
            person.setCreatedAt(OffsetDateTime.now().minusDays(random.nextInt(365)));
            person.setUpdatedAt(OffsetDateTime.now());
            person.setCreated_who("spring-app");
            person.setRole(roles.get(random.nextInt(roles.size() - 1) + 1));

            personList.add(person);
        });
        peopleRepository.saveAll(personList);

    }
}