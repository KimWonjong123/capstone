package capstone.capbackend.service;

import capstone.capbackend.dto.CreateChatResponseDTO;
import capstone.capbackend.dto.JoiningChatDTO;
import capstone.capbackend.entity.Chat;
import capstone.capbackend.entity.User;
import capstone.capbackend.entity.UserChat;
import capstone.capbackend.repository.ChatRepository;
import capstone.capbackend.repository.UserChatRepository;
import capstone.capbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserChatRepository userChatRepository;

    @Transactional
    public Mono<CreateChatResponseDTO> createChat(Long ownerId, String chatName) {
        Mono<User> user = userRepository.findById(ownerId);
        return chatRepository.findByOwnerIdAndName(ownerId, chatName)
                .defaultIfEmpty(Chat.builder().build())
                .flatMap(chat -> Mono.zip(
                        Mono.just(chat),
                        userRepository.findById(ownerId)
                ))
                .flatMap(tuple -> {
                    if (tuple.getT1().getId() != null) {
                        return Mono.error(new IllegalArgumentException("Chat already exists"));
                    }
                    return chatRepository.save(
                            Chat.builder()
                                    .ownerId(ownerId)
                                    .ownerName(tuple.getT2().getNickname())
                                    .name(chatName)
                                    .insertTime(LocalDateTime.now())
                                    .updateTime(LocalDateTime.now())
                                    .build()
                    );
                })
                .flatMap(chat -> Mono.just(CreateChatResponseDTO.builder().chatId(chat.getId()).chatName(chat.getName()).build()
                ));
    }

    public Flux<JoiningChatDTO> searchChat(String name, Long userId) {
        return chatRepository.findByIdNotAndNameContainingOrderByInsertTimeDesc(userId, name)
                .flatMap(chat -> Flux.just(JoiningChatDTO.builder()
                        .chatId(chat.getId())
                        .chatName(chat.getName())
                        .ownerId(chat.getOwnerId())
                        .ownerName(chat.getOwnerName())
                        .insertTime(chat.getInsertTime())
                        .build())
                );
    }

    public Mono<UserChat> joinChat(Long chatId, Long userId) {
        return userChatRepository.findByUserIdAndChatId(userId, chatId)
                .defaultIfEmpty(UserChat.builder().build())
                .flatMap(userChat -> Mono.zip(
                        Mono.just(userChat),
                        userRepository.findById(userId)
                ))
                .flatMap(tuple -> {
                    if (tuple.getT1().getChatId() != null) {
                        return Mono.error(new IllegalArgumentException("User already joined"));
                    }
                    User user = tuple.getT2();
                    return userChatRepository.save(
                            UserChat.builder()
                                    .chatId(chatId)
                                    .userId(user.getId())
                                    .userName(user.getNickname())
                                    .insertTime(LocalDateTime.now())
                                    .lastChatTime(LocalDateTime.now())
                                    .build()
                    );
                });
    }
}
