package capstone.capbackend.service;

import capstone.capbackend.dto.ChatInfoDTO;
import capstone.capbackend.dto.CreateChatResponseDTO;
import capstone.capbackend.dto.JoiningChatDTO;
import capstone.capbackend.entity.Chat;
import capstone.capbackend.entity.ChatMessage;
import capstone.capbackend.entity.User;
import capstone.capbackend.entity.UserChat;
import capstone.capbackend.repository.ChatMessageRepository;
import capstone.capbackend.repository.ChatRepository;
import capstone.capbackend.repository.UserChatRepository;
import capstone.capbackend.repository.UserRepository;
import capstone.capbackend.vo.BinaryResultVO;
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
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public Mono<CreateChatResponseDTO> createChat(Long ownerId, String chatName) {
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
                                    .lastChatTime(LocalDateTime.now())
                                    .build()
                    );
                })
                .flatMap(chat -> userChatRepository.save(
                        UserChat.builder()
                                .chatId(chat.getId())
                                .userId(ownerId)
                                .userName(chat.getOwnerName())
                                .insertTime(LocalDateTime.now())
                                .build()
                ).thenReturn(chat))
                .flatMap(chat -> Mono.just(CreateChatResponseDTO.builder().chatId(chat.getId()).chatName(chat.getName()).build()
                ));
    }

    public Flux<JoiningChatDTO> searchChat(String name, Long userId) {
        return chatRepository.findByOwnerIdNotAndNameContainingOrderByInsertTimeDesc(userId, name)
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
                                    .build()
                    );
                });
    }

    public Flux<ChatInfoDTO> getJoiningChats(Long userId) {
        return userChatRepository.findByUserId(userId)
                .flatMap(userChat -> Mono.zip(
                        chatRepository.findById(userChat.getChatId()),
                        userRepository.findById(userChat.getUserId()),
                        Mono.just(userChat)
                ))
                .flatMap(tuple -> Flux.just(ChatInfoDTO.builder()
                        .chatId(tuple.getT1().getId())
                        .chatName(tuple.getT1().getName())
                        .userId(tuple.getT1().getOwnerId())
                        .userName(tuple.getT1().getOwnerName())
                        .lastChatTime(tuple.getT1().getLastChatTime())
                        .userChatId(tuple.getT3().getId())
                        .build()).sort((a, b) -> b.getLastChatTime().compareTo(a.getLastChatTime())));
    }

    public Flux<ChatInfoDTO> getCreatedChats(Long userId) {
        return chatRepository.findByOwnerIdOrderByLastChatTimeDesc(userId)
                .flatMap(chat -> Flux.zip(
                        Mono.just(chat),
                        userChatRepository.findByUserIdAndChatId(userId, chat.getId())
                ))
                .flatMap(tuple -> Flux.just(ChatInfoDTO.builder()
                        .chatId(tuple.getT1().getId())
                        .chatName(tuple.getT1().getName())
                        .userId(tuple.getT1().getOwnerId())
                        .userName(tuple.getT1().getOwnerName())
                        .lastChatTime(tuple.getT1().getLastChatTime())
                        .userChatId(tuple.getT2().getId())
                        .build()));
    }

    public Flux<ChatMessage> getMessages(Long userChatId) {
        return userChatRepository.findById(userChatId)
                .defaultIfEmpty(UserChat.builder().build())
                .flatMap(userChat -> {
                    if (userChat.getChatId() == null) return Mono.error(new IllegalArgumentException("Chat not found"));
                    else return Mono.just(userChat);
                })
                .flatMapMany(userChat -> chatMessageRepository.findByChatIdOrderByInsertTimeAsc(userChat.getChatId()));
    }

    public Flux<ChatMessage> sendMessage(Long userChatId, Long userId, String message) {
        return userChatRepository.findById(userChatId)
                .defaultIfEmpty(UserChat.builder().build())
                .flatMap(userChat -> {
                    if (userChat.getChatId() == null) return Mono.error(new IllegalArgumentException("Chat not found"));
                    else return Mono.just(userChat);
                })
                .flatMap(userChat -> userRepository.findById(userId)
                        .flatMap(user -> chatMessageRepository.save(
                                ChatMessage.builder()
                                        .chatId(userChat.getChatId())
                                        .userChatId(userChat.getId())
                                        .userId(user.getId())
                                        .userName(user.getNickname())
                                        .message(message)
                                        .insertTime(LocalDateTime.now())
                                        .build()
                        ))
                        .then(chatRepository.findById(userChat.getChatId())
                                .flatMap(chat -> {
                                    chat.setLastChatTime(LocalDateTime.now());
                                    return chatRepository.save(chat);
                                }))
                        .thenReturn(userChat)
                )
                .flatMapMany(userchat -> chatMessageRepository.findByChatIdOrderByInsertTimeAsc(userchat.getChatId()));
    }

    public Mono<BinaryResultVO> leaveChat(Long userChatId, Long userId) {
        return userChatRepository.findById(userChatId)
                .defaultIfEmpty(UserChat.builder().build())
                .flatMap(userChat -> Mono.zip(
                        Mono.just(userChat),
                        chatRepository.findById(userChat.getChatId())
                ))
                .flatMap(tuple -> {
                    if (tuple.getT1().getChatId() == null || tuple.getT2().getOwnerId().equals(userId)) return Mono.just(false);
                    return chatMessageRepository.deleteAllByUserId(userId)
                            .then(userChatRepository.delete(tuple.getT1()))
                            .thenReturn(true);
                })
                .flatMap(result -> result ? Mono.just(new BinaryResultVO(true)) : Mono.just(new BinaryResultVO(false)));
    }

    public Mono<BinaryResultVO> deleteChat(Long userChatId, Long userId) {
        return userChatRepository.findById(userChatId)
                .flatMap(userChat -> chatRepository.findById(userChat.getChatId())
                        .defaultIfEmpty(Chat.builder().build())
                        .flatMap(chat -> {
                            if (chat.getId() == null || !chat.getOwnerId().equals(userId)) return Mono.just(new BinaryResultVO(false));
                            else return chatMessageRepository.deleteAllByChatId(chat.getId())
                                    .then(userChatRepository.delete(userChat))
                                    .then(chatRepository.delete(chat))
                                    .thenReturn(new BinaryResultVO(true));
                        }));
    }
}
