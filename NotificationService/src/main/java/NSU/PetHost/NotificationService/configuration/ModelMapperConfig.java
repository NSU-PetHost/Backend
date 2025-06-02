package NSU.PetHost.NotificationService.configuration;

import NSU.PetHost.NotificationService.api.dto.NotificationScheduleRequest;
import NSU.PetHost.NotificationService.core.model.NotificationSchedule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mapper.typeMap(NotificationScheduleRequest.class, NotificationSchedule.class)
              .addMappings(m -> m.skip(NotificationSchedule::setId));

        
        return mapper;
    }
}