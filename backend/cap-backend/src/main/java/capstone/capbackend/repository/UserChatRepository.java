package capstone.capbackend.repository;

import capstone.capbackend.entity.UserChat;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserChatRepository extends R2dbcRepository<UserChat, Long> {
    Flux<UserChat> findByUserIdOrderByLastChatTimeDesc(Long userId);

    Mono<UserChat> findByUserIdAndChatId(Long userId, Long chatId);
}
