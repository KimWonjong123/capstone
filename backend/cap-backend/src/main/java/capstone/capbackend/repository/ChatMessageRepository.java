package capstone.capbackend.repository;

import capstone.capbackend.entity.ChatMessage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends R2dbcRepository<ChatMessage, Long> {
    Flux<ChatMessage> findByUserChatIdOrderByInsertTimeDesc(Long userChatId);
}
