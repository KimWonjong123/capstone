package capstone.capbackend.entity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash
public record RefreshToken(
        @Id
        String refreshToken,
        @NotNull Long userId,
        String userOauthType
) {}
