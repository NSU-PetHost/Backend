package NSU.PetHost.AuthService.DBInit;

import NSU.PetHost.AuthService.models.Role;
import NSU.PetHost.AuthService.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                            new Role("ROOT"),
                            new Role("ADMIN"),
                            new Role("USER"),
                            new Role("ANONYMOUS")
                    )
            );
        }
    }

}
