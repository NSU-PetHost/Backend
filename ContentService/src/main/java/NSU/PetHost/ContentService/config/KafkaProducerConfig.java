package NSU.PetHost.ContentService.config;

import NSU.PetHost.ContentService.dto.responses.kafka.KafkaArticleCreated;
import NSU.PetHost.ContentService.dto.responses.kafka.KafkaArticleUpdated;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        // чтобы разрешить любые пакеты для десериализации (при использовании @KafkaListener)
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public ProducerFactory<Long, KafkaArticleCreated> articleCreatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, KafkaArticleCreated> articleCreatedKafkaTemplate() {
        return new KafkaTemplate<>(articleCreatedProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, KafkaArticleUpdated> articleUpdatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, KafkaArticleUpdated> articleUpdatedKafkaTemplate() {
        return new KafkaTemplate<>(articleUpdatedProducerFactory());
    }

}
