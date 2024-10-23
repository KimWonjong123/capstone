package capstone.capbackend.service;

import capstone.capbackend.repository.RefreshTokenRepository;
import capstone.capbackend.repository.UserRepository;
import capstone.capbackend.util.JwtUtil;
import capstone.capbackend.util.NicknameGenerationUtil;
import capstone.capbackend.vo.JwtTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final NicknameGenerationUtil nicknameGenerationUtil;

    public Mono<JwtTokenVO> reissueToken(String rt) {
        return jwtUtil.reissueTokens(rt);
    }

    public Mono<JwtTokenVO> testLogin(String id) {
        return userRepository.findByUserId(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> jwtUtil.generateAccessToken(user)
                        .zipWith(jwtUtil.generateRefreshToken(user))
                        .map(tuple -> new JwtTokenVO(tuple.getT1(), tuple.getT2().refreshToken()))
                );
    }
}
