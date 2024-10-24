package capstone.capbackend.repository;

import capstone.capbackend.entity.Chat;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRepository extends R2dbcRepository<Chat, Long> {
    Flux<Chat> findByOwnerId(Long userId);

    Flux<Chat> findByChatId(Long chatId);
}
