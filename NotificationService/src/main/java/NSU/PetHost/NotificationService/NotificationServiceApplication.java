package NSU.PetHost.NotificationService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@OpenAPIDefinition(info = @Info(title = "Notification API", version = "v1"))
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer",
		in = SecuritySchemeIn.HEADER
)
public class NotificationServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}
}
