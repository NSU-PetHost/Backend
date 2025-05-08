package NSU.PetHost.AuthService.DBInit;

import NSU.PetHost.AuthService.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainDataInitializer implements CommandLineRunner {

    private final RoleInitializer roleInitializer;
    private final PersonInitializer personInitializer;

    @Override
    public void run(String... args) throws Exception {
        roleInitializer.run();
        personInitializer.run();
    }
}
