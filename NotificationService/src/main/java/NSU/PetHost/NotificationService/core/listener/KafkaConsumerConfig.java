package NSU.PetHost.NotificationService.core.listener;

import NSU.PetHost.NotificationService.api.dto.kafka.VerifyCodeMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, VerifyCodeMessage> verifyCodeMessageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // настройка JsonDeserializer для значения
        JsonDeserializer<VerifyCodeMessage> valueDeserializer = new JsonDeserializer<>(VerifyCodeMessage.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(true);

        // оборачиваем десериализаторы в ErrorHandlingDeserializer
        ErrorHandlingDeserializer<String> keyErrorDeserializer = new ErrorHandlingDeserializer<>(new StringDeserializer());
        ErrorHandlingDeserializer<VerifyCodeMessage> valueErrorDeserializer = new ErrorHandlingDeserializer<>(valueDeserializer);
        
        return new DefaultKafkaConsumerFactory<>(props, keyErrorDeserializer, valueErrorDeserializer);
    }

    @Bean("verifyCodeMessageKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, VerifyCodeMessage> verifyCodeMessageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VerifyCodeMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(verifyCodeMessageConsumerFactory());
        // можно настроить обработчик ошибок, если десериализация полностью провалится
        // factory.setErrorHandler(new SeekToCurrentErrorHandler(new FixedBackOff(1000L, 2)));
        return factory;
    }
}