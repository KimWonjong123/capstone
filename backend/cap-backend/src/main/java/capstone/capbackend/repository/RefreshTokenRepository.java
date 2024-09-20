package capstone.capbackend.repository;

import capstone.capbackend.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public Mono<RefreshToken> save(RefreshToken refreshToken) {
        return redisTemplate.opsForValue()
                .set(refreshToken.refreshToken(), refreshToken, Duration.ofDays(14))
                .flatMap(result -> result ? Mono.just(refreshToken): Mono.error(new RuntimeException("REDIS ERROR")));
    }

    public Mono<String> updateRefreshToken(String refreshToken, String newRefreshToken) {
        return Mono.from(
                redisTemplate.execute(
                        RedisScript.of(new ClassPathResource("redis-script/issue-new-token.lua")),
                        List.of(refreshToken), List.of(newRefreshToken)
                ));
    }

    public Mono<Boolean> deleteRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue()
                .delete(refreshToken);
    }

}
