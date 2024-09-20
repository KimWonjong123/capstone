package capstone.capbackend.vo;

public record JwtTokenVO(
        String accessToken,
        String refreshToken
) {
}
