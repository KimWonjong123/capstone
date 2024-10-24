package capstone.capbackend.service;

import capstone.capbackend.dto.CreateChatResponseDTO;
import capstone.capbackend.entity.Chat;
import capstone.capbackend.entity.UserChat;
import capstone.capbackend.repository.ChatRepository;
import capstone.capbackend.repository.UserChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    @Transactional
    public Mono<CreateChatResponseDTO> createChat(Long ownerId, String chatName) {
        return chatRepository.findByOwnerIdAndName(ownerId, chatName)
                .defaultIfEmpty(Chat.builder().build())
                .flatMap(existingChat -> {
                    if (existingChat.getId() != null) {
                        return Mono.error(new IllegalArgumentException("Chat already exists"));
                    }
                    return chatRepository.save(
                            Chat.builder()
                                    .ownerId(ownerId)
                                    .name(chatName)
                                    .insertTime(LocalDateTime.now())
                                    .updateTime(LocalDateTime.now())
                                    .build()
                    );
                })
                .flatMap(chat -> Mono.just(CreateChatResponseDTO.builder().chatId(chat.getId()).chatName(chat.getName()).build()
                ));
    }
}
