package NSU.PetHost.AuthService.config;

import NSU.PetHost.AuthService.models.VerifyCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                // глобально задаём TTL для всех кэшей, если не переопределено в @Cacheable
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues();
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
        return RedisCacheManager.builder(cf)
                .cacheDefaults(cacheConfiguration())
                .build();
    }

    @Bean
    public RedisTemplate<String, VerifyCode> redisTemplate() {
        RedisTemplate<String, VerifyCode> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Использование JSON-сериализатора для значений
        Jackson2JsonRedisSerializer<VerifyCode> serializer = new Jackson2JsonRedisSerializer<>(VerifyCode.class);
        template.setDefaultSerializer(serializer);
        template.setValueSerializer(serializer);
        return template;
    }

}
