package NSU.PetHost.AuthService;

import NSU.PetHost.AuthService.config.RedisConfig;
import NSU.PetHost.AuthService.config.SecurityConfig;
import NSU.PetHost.AuthService.config.SpringConfig;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@Import({SecurityConfig.class, RedisConfig.class, SpringConfig.class})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}
