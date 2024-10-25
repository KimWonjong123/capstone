package capstone.capbackend.repository;

import capstone.capbackend.entity.Chat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends ReactiveCrudRepository<Chat, Long> {
    Flux<Chat> findByOwnerIdOrderByLastChatTimeDesc(Long userId);

    Mono<Chat> findByOwnerIdAndName(Long ownerId, String name);

    Flux<Chat> findByOwnerIdNotAndNameContainingOrderByInsertTimeDesc(Long userId, String name);

    Mono<Chat> findById(Long chatId);

    Mono<Void> deleteAllByOwnerId(Long userId);

}
