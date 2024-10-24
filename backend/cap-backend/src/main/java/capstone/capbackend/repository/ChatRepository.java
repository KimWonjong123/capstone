package capstone.capbackend.repository;

import capstone.capbackend.entity.Chat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends ReactiveCrudRepository<Chat, Long> {
    Flux<Chat> findByOwnerIdOrderByInsertTimeDesc(Long userId);

    Flux<Chat> findByOwnerIdAndNameContaining(Long userId, String name);

    Mono<Chat> findByOwnerIdAndName(Long userId, String name);

    Flux<Chat> findByIdNotAndNameContainingOrderByInsertTimeDesc(Long userId, String name);

    Mono<Chat> findById(Long chatId);
}
