package NSU.PetHost.AuthService.config;

import NSU.PetHost.AuthService.models.VerifyCode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        // доверяем свои модели
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "NSU.PetHost.AuthService.models");
        // если в заголовках нет type info, указываем класс по-умолчанию
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, VerifyCode.class);
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, VerifyCode> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, VerifyCode> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, VerifyCode> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new LongDeserializer(),
                // этот деструктер сам возьмёт props.get(VALUE_DEFAULT_TYPE) и TRUSTED_PACKAGES
                new JsonDeserializer<>(VerifyCode.class));
    }
}
