package NSU.PetHost.ContentService.config;

import NSU.PetHost.ContentService.dto.responses.positive.ArticleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
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
@EnableCaching
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
    public RedisTemplate<String, ArticleResponse> redisTemplate() {
        RedisTemplate<String, ArticleResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Использование JSON-сериализатора для значений
        Jackson2JsonRedisSerializer<ArticleResponse> serializer = new Jackson2JsonRedisSerializer<>(ArticleResponse.class);
        template.setDefaultSerializer(serializer);
        template.setValueSerializer(serializer);
        return template;
    }

}
