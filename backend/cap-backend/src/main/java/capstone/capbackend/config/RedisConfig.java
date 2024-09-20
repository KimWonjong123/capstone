package capstone.capbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${secret.redis.url}")
    private String REDIS_HOST;

    @Value("${secret.redis.port}")
    private int REDIS_PORT;

    @Value("${secret.redis.password}")
    private String REDIS_PWD;

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        var redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(REDIS_HOST);
        redisConfig.setPort(REDIS_PORT);
        redisConfig.setPassword(REDIS_PWD);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> redisOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Object> context =
                builder
                .value(serializer)
                .hashValue(serializer)
                .hashKey(serializer)
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }

}
