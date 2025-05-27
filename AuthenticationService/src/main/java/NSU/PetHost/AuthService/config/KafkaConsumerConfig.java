package NSU.PetHost.AuthService.config;

import NSU.PetHost.AuthService.dto.responses.kafka.KafkaArticleCreated;
import NSU.PetHost.AuthService.dto.responses.kafka.KafkaArticleUpdated;
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

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "NSU.PetHost.AuthService.models, NSU.PetHost.AuthService.dto.responses.kafka");
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, VerifyCode> verifyCodeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, VerifyCode> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(verifyCodeConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, VerifyCode> verifyCodeConsumerFactory() {
        Map<String, Object> props = baseConsumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, VerifyCode.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new LongDeserializer(),
                new JsonDeserializer<>(VerifyCode.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, KafkaArticleCreated> articleCreatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, KafkaArticleCreated> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(articleCreatedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, KafkaArticleCreated> articleCreatedConsumerFactory() {
        Map<String, Object> props = baseConsumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaArticleCreated.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new LongDeserializer(),
                new JsonDeserializer<>(KafkaArticleCreated.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, KafkaArticleUpdated> articleUpdatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, KafkaArticleUpdated> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(articleUpdatedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, KafkaArticleUpdated> articleUpdatedConsumerFactory() {
        Map<String, Object> props = baseConsumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaArticleUpdated.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new LongDeserializer(),
                new JsonDeserializer<>(KafkaArticleUpdated.class));
    }

}
