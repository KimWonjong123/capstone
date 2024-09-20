package capstone.capbackend.service;

import capstone.capbackend.dto.KakaoUserInfoResponseDTO;
import capstone.capbackend.dto.KakaoUserTokenResponseDTO;
import capstone.capbackend.dto.OauthTokenDTO;
import capstone.capbackend.dto.interfaces.OauthUserinfoDTO;
import capstone.capbackend.entity.User;
import capstone.capbackend.repository.UserRepository;
import capstone.capbackend.util.JwtUtil;
import capstone.capbackend.util.NicknameGenerationUtil;
import capstone.capbackend.util.OauthUtil;
import capstone.capbackend.vo.BinaryResultVO;
import capstone.capbackend.vo.JwtTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOauthService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final NicknameGenerationUtil nicknameGenerationUtil;
    private final OauthUtil oauthUtil;

    @Value("${secret.kakao.client-id}")
    private String KAKAO_CLIENT_ID;


    @Value("${secret.kakao.redirect-url}")
    private String KAKAO_REDIRECT_URL;

    private static final String KAKAO_UNLINK_URI = "/v1/user/unlink";
    private static final String KAKAO_PUBLIC_KEY_URI = "/.well-known/jwks.json";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com";
    private static final String KAKAO_TOKEN_URI = "/oauth/token";
    private static final String KAKAO_USERINFO_URL = "https://kapi.kakao.com";
    private static final String KAKAO_USERINFO_URI = "/v2/user/me";
    private static final String KAKAO_OAUTH_TYPE = "KAKAO";
    private static final String OAUTH_NO_PASSWORD = "OAUTH-NO-PASSWORD";

    public Mono<KakaoUserTokenResponseDTO> getUserTokens(@NonNull String code, boolean isUnlink) {
        return webClient.mutate()
                .baseUrl(KAKAO_TOKEN_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
                .build()
                .post()
                .uri(KAKAO_TOKEN_URI)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", KAKAO_CLIENT_ID)
                        .with("code", code)
                        .with("redirect_uri", this.getRedirectUrl(isUnlink)))
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(KakaoUserTokenResponseDTO.class)
                .log();
    }

    public Mono<? extends OauthUserinfoDTO> getUserInfos(@NonNull OauthTokenDTO token) {
        return Mono.zip(
                webClient.mutate()
                        .baseUrl(KAKAO_USERINFO_URL)
                        .build()
                        .post()
                        .uri(KAKAO_USERINFO_URI)
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                        .retrieve()
                        .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                                clientResponse -> clientResponse.bodyToMono(String.class).map(RuntimeException::new))
                        .bodyToMono(KakaoUserInfoResponseDTO.class),
                oauthUtil.getPublicKey(KAKAO_OAUTH_TYPE, KAKAO_TOKEN_URL, KAKAO_PUBLIC_KEY_URI, token)
                        .map(key -> oauthUtil.parseClaims(token.getIdToken(), key))
                        .map(claims -> {
                            // verify claims
                            if (!claims.getIssuer().equals(KAKAO_TOKEN_URL)) {
                                throw new RuntimeException("INVALID KAKAO ID TOKEN ISSUER");
                            }
                            if (!claims.getAudience().equals(KAKAO_CLIENT_ID)) {
                                throw new RuntimeException("INVALID KAKAO ID TOKEN AUDIENCE");
                            }
                            if (new Date().after(claims.getExpiration())) {
                                throw new RuntimeException("EXPIRED KAKAO ID TOKEN");
                            }

                            return claims.getSubject();
                        })
        )
                .map(tuple -> {
                    KakaoUserInfoResponseDTO userInfo = tuple.getT1();
                    userInfo.setSub(tuple.getT2());
                    return userInfo;
                })
                .log();
    }

    public <T extends OauthUserinfoDTO> Mono<JwtTokenVO> signUp(T userInfo) {
        return userRepository.getUniqueNickname(nicknameGenerationUtil.generateRandomNickname())
                .flatMap(nickname -> userRepository
                                    .save(User.builder()
                                            .userId(userInfo.getUserId())
                                            .nickname(nickname)
                                            .oauthType(KAKAO_OAUTH_TYPE)
                                            .oauthSub(userInfo.getSub())
                                            .build()))
                .flatMap(user -> Mono.zip(
                                jwtUtil.generateAccessToken(user),
                                jwtUtil.generateRefreshToken(user)
                        )
                        .map(tokens -> new JwtTokenVO(tokens.getT1(), tokens.getT2().refreshToken()))
                );
    }

    @Transactional
    public <T extends OauthUserinfoDTO> Mono<JwtTokenVO> login(T userinfo) {
        return userRepository
                .findByUserId(userinfo.getUserId())
                .defaultIfEmpty(new User())
                .flatMap(user -> {
                    if (user.getUserId() == null) return signUp(userinfo);
                    if (!KAKAO_OAUTH_TYPE.equals(user.getOauthType())) {
                        return Mono.error(new RuntimeException("USER IS " + user.getOauthType() + " OAUTH-USER"));
                    }
                    return Mono.zip(
                                    jwtUtil.generateAccessToken(user),
                                    jwtUtil.generateRefreshToken(user)
                            )
                            .map(tokens -> new JwtTokenVO(tokens.getT1(), tokens.getT2().refreshToken())
                    );
                })
                .log();
    }

    @Transactional
    public Mono<BinaryResultVO> unlinkOauthAccount(OauthTokenDTO dto, boolean isFallback) {
        return this.getUserInfos(dto)
                .flatMap(userinfo -> {
                    if (isFallback) return requestUnlink(dto);
                    return Mono.defer(() -> userRepository.findByUserId(userinfo.getUserId())
                            .switchIfEmpty(Mono.error(new RuntimeException("USER NOT FOUND")))
                            .flatMap(user -> {
                                if (!KAKAO_OAUTH_TYPE.equals(user.getOauthType())) {
                                    return Mono.error(new RuntimeException("USER IS " + user.getOauthType() + " OAUTH-USER"));
                                }
                                return requestUnlink(dto);
                            }));
                });
    }

    private Mono<BinaryResultVO> requestUnlink(OauthTokenDTO dto) {
        return webClient.mutate()
                .baseUrl(KAKAO_USERINFO_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
                .build()
                .post()
                .uri(KAKAO_UNLINK_URI)
                .header("Authorization", "Bearer " + dto.getAccessToken())
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(Void.class)
                .then(Mono.defer(() -> Mono.just(new BinaryResultVO(true))));
    }

    private String getRedirectUrl(boolean isUnlinked) {
        return KAKAO_REDIRECT_URL + (isUnlinked ? "/unlink" : "");
    }

    public Mono<String> getOauthUrl() {
        return Mono.just(
       KAKAO_TOKEN_URL
                    + "/oauth/authorize?client_id=" + KAKAO_CLIENT_ID
                    + "&redirect_uri="              + KAKAO_REDIRECT_URL
                    + "&response_type=code"
        );
    }

}
