package capstone.capbackend.repository;

import capstone.capbackend.entity.UserChat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserChatRepository extends ReactiveCrudRepository<UserChat, Long> {
    Mono<UserChat> findByUserIdAndChatId(Long userId, Long chatId);

    Flux<UserChat> findByUserId(Long userId);

    Mono<Void> deleteAllByUserId(Long userId);

    Mono<Void> deleteAllByChatId(Long chatId);
}
