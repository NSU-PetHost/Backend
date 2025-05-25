package NSU.PetHost.NotificationService;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class NotificationServiceApplication {
	public static void main(String[] args) {
		System.out.println("Hello World");
		SpringApplication.run(NotificationServiceApplication.class, args);
	}
}
