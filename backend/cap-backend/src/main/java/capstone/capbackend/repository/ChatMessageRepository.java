package capstone.capbackend.repository;

import capstone.capbackend.entity.ChatMessage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends ReactiveCrudRepository<ChatMessage, Long> {
    Flux<ChatMessage> findByUserChatIdOrderByInsertTimeDesc(Long userChatId);

}
