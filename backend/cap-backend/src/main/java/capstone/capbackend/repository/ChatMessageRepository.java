package capstone.capbackend.repository;

import capstone.capbackend.entity.ChatMessage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatMessageRepository extends ReactiveCrudRepository<ChatMessage, Long> {
    Flux<ChatMessage> findByChatIdOrderByInsertTimeAsc(Long userChatId);

    Mono<Void> deleteAllByUserId(Long userChatId);

    Mono<Void> deleteAllByChatId(Long chatId);

}
