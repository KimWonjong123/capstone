package capstone.capbackend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash
public record RefreshToken(
        @Id
    String refreshToken,
        Long userId,
        String userOauthType
) {}
