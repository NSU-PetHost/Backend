package NSU.PetHost.AuthService.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class SpringConfig {

    @Bean
    @Scope("singleton")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @Scope("singleton")
    public SimpleMailMessage simpleMailMessage() {
        return new SimpleMailMessage();
    }

}
