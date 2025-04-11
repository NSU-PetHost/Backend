package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.models.VerifyCode;
import NSU.PetHost.AuthService.repositories.RedisRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisService implements RedisRepository {
    private static final String KEY_PREFIX = "verifyCode:"; // Каждый VerifyCode хранится в своем ключе
    private final RedisTemplate<String, VerifyCode> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, VerifyCode> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void add(final VerifyCode verifyCode) {
        String key = KEY_PREFIX + verifyCode.getEmail();
        // Сохраняем объект с TTL в 5 минут
        redisTemplate.opsForValue().set(key, verifyCode, 5, TimeUnit.MINUTES);
    }

    @Override
    public void delete(final String email) {
        String key = KEY_PREFIX + email;
        redisTemplate.delete(key);
    }

    @Override
    public VerifyCode findVerifyCode(String email) {
        String key = KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }
}
