package capstone.capbackend.service;

import capstone.capbackend.entity.User;
import capstone.capbackend.repository.*;
import capstone.capbackend.util.JwtUtil;
import capstone.capbackend.util.NicknameGenerationUtil;
import capstone.capbackend.vo.BinaryResultVO;
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
    private final UserChatRepository userChatRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtUtil jwtUtil;

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

    public Mono<User> getMe(Long userId) {
        return userRepository.findById(userId);
    }

    public Mono<BinaryResultVO> deleteAccount(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> chatMessageRepository.deleteAllByUserId(userId)
                        .then(userChatRepository.deleteAllByUserId(userId))
                        .then(chatRepository.deleteAllByOwnerId(userId))
                        .then(userRepository.delete(user))
                        .then(Mono.just(user)
                        ))
                .flatMap(user -> userRepository.delete(user)
                        .then(Mono.just(new BinaryResultVO(true)))
                );
    }
}
