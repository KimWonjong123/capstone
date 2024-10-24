package capstone.capbackend.util;

import capstone.capbackend.entity.RefreshToken;
import capstone.capbackend.entity.User;
import capstone.capbackend.repository.RefreshTokenRepository;
import capstone.capbackend.vo.JwtTokenVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${secret.jwt.key}")
    private String SECRET_KEY;

    private final RefreshTokenRepository refreshTokenRepository;
    private static final int ACCESS_TOKEN_EXPIRES = 1000 * 60 * 60;
    private static final String CLAIM_OAUTH_TYPE = "OAUTH_TYPE";

    public Mono<String> generateAccessToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put(CLAIM_OAUTH_TYPE, user.getOauthType());
        String accessToken = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRES))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Mono.justOrEmpty(accessToken);
    }

    public Mono<RefreshToken> generateRefreshToken(User user) {
        return refreshTokenRepository
                .save(new RefreshToken(UUID.randomUUID().toString(), user.getUserId(), user.getOauthType()));
    }

    public Mono<JwtTokenVO> reissueTokens(String rt) {
        return Mono.just(UUID.randomUUID().toString())
                .flatMap(newRt -> refreshTokenRepository.updateRefreshToken(rt, newRt)
                        .flatMap(res -> {
                            if ("FAIL".equalsIgnoreCase(res)) {
                                return Mono.error(new RuntimeException("FAILED TO RE-ISSUE TOKEN"));
                            }
                            String[] infos = res.split(":");
                            return Mono.just(User.builder()
                                    .userId(infos[0])
                                    .oauthType(infos[1])
                                    .build());
                        })
                        .flatMap(user -> Mono.zip(
                                        generateAccessToken(user),
                                        Mono.just(newRt)
                                ).map(tokens -> new JwtTokenVO(tokens.getT1(), tokens.getT2()))
                        )
                );
    }

    public Mono<Claims> parseClaims(String accessToken) {
        try {
            return Mono.just(
                    Jwts.parserBuilder()
                            .setSigningKey(getKey())
                            .build()
                            .parseClaimsJws(accessToken)
                            .getBody()
            );
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            log.info("{} TOKEN IS EXPIRED : {}", claims.getSubject(), e.getMessage());
            return Mono.justOrEmpty(claims);
        }
    }

    public Mono<String> extractToken(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION))
                .defaultIfEmpty(Strings.EMPTY);
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
}
