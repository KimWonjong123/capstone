package capstone.capbackend.repository;

import capstone.capbackend.entity.UserChat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserChatRepository extends ReactiveCrudRepository<UserChat, Long> {
    Flux<UserChat> findByUserIdOrderByLastChatTimeDesc(Long userId);

    Mono<UserChat> findByUserIdAndChatId(Long userId, Long chatId);

    Flux<UserChat> findByChatIdOrderByLastChatTimeDesc(Long chatId);

    Mono<Void> deleteAllByUserId(Long userId);
}
