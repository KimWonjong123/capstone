package capstone.capbackend.controller;

import capstone.capbackend.dto.OauthTokenDTO;
import capstone.capbackend.service.KakaoOauthService;
import capstone.capbackend.service.UserService;
import capstone.capbackend.vo.BinaryResultVO;
import capstone.capbackend.vo.JwtTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final KakaoOauthService kakaoOauthService;

    @PostMapping("/token/reissue")
    public Mono<JwtTokenVO> reissueToken(@RequestParam("rt") String rt) {
        return userService.reissueToken(rt);
    }

    @GetMapping("/oauth/kakao/url")
    public Mono<String> kakaoOauthUrl() {
        return kakaoOauthService.getOauthUrl();
    }

    @GetMapping("/oauth/kakao")
    public Mono<JwtTokenVO> kakaoOauth(@RequestParam("code") String code) {
        Mono<? extends OauthTokenDTO> cached = kakaoOauthService.getUserTokens(code, false).cache();
        return cached
                .flatMap(kakaoOauthService::getUserInfos)
                .flatMap(kakaoOauthService::login)
                .onErrorResume(e ->
                        cached.flatMap(token ->
                                kakaoOauthService.unlinkOauthAccount(token, true)
                                        .then(Mono.error(new RuntimeException("KAKAO OAUTH FAILED")))
                        )
                );
    }

    @GetMapping("/oauth/kakao/unlink")
    public Mono<BinaryResultVO> kakaoUnlink(@RequestParam("code") String code) {
        Mono<? extends OauthTokenDTO> cached = kakaoOauthService.getUserTokens(code, true).cache();
        return cached
                .flatMap(token -> kakaoOauthService.unlinkOauthAccount(token, false))
                .onErrorResume(e ->
                        cached.flatMap(token ->
                                kakaoOauthService.unlinkOauthAccount(token, true)
                                        .then(Mono.error(new RuntimeException("KAKAO OAUTH UNLINK FAILED" +
                                                " - User is not linked with KAKAO OAUTH")))
                        )
                );
    }
}
