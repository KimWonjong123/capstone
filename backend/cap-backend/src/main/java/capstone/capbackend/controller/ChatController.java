package capstone.capbackend.controller;

import capstone.capbackend.config.security.UserInfo;
import capstone.capbackend.config.security.UserPrincipal;
import capstone.capbackend.dto.ChatInfoDTO;
import capstone.capbackend.dto.CreateChatRequestDTO;
import capstone.capbackend.dto.CreateChatResponseDTO;
import capstone.capbackend.dto.JoiningChatDTO;
import capstone.capbackend.entity.ChatMessage;
import capstone.capbackend.entity.UserChat;
import capstone.capbackend.service.ChatService;
import capstone.capbackend.vo.BinaryResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("")
    public Mono<CreateChatResponseDTO> createChat(@RequestBody CreateChatRequestDTO requestDTO, @UserInfo UserPrincipal user) {
        return chatService.createChat(Long.parseLong(user.getUsername()), requestDTO.getName());
    }

    @GetMapping("/search")
    public Flux<JoiningChatDTO> searchChat(@RequestParam("name") String name, @UserInfo UserPrincipal user) {
        return chatService.searchChat(name, Long.parseLong(user.getUsername()));
    }

    @PostMapping("/join")
    public Mono<UserChat> joinChat(@RequestParam("chatId") Long chatId, @UserInfo UserPrincipal user) {
        return chatService.joinChat(chatId, Long.parseLong(user.getUsername()));
    }

    @GetMapping("/list/joining")
    public Flux<ChatInfoDTO> getJoiningChats(@UserInfo UserPrincipal user) {
        return chatService.getJoiningChats(Long.parseLong(user.getUsername()));
    }

    @GetMapping("/list/created")
    public Flux<ChatInfoDTO> getCreatedChats(@UserInfo UserPrincipal user) {
        return chatService.getCreatedChats(Long.parseLong(user.getUsername()));
    }

//    @GetMapping("/list/created/joined")
//    public Flux<ChatInfoDTO> getJoinedCreatedChats(@RequestParam("chatId") Long chatId, @UserInfo UserPrincipal user) {
//        return chatService.getJoinedCreatedChats(chatId, Long.parseLong(user.getUsername()));
//    }

    @GetMapping("/messages")
    public Flux<ChatMessage> getMessages(@RequestParam("userChatId") Long userChatId) {
        return chatService.getMessages(userChatId);
    }

    @PostMapping("/messages")
    public Flux<ChatMessage> sendMessage(@RequestParam("userChatId") Long userChatId,
                                         @RequestParam("message") String message,
                                         @UserInfo UserPrincipal user) {
        return chatService.sendMessage(userChatId, Long.parseLong(user.getUsername()), message);
    }

    @PostMapping("/leave")
    public Mono<BinaryResultVO> leaveChat(@RequestParam("userChatId") Long userChatId, @UserInfo UserPrincipal user) {
        return chatService.leaveChat(userChatId, Long.parseLong(user.getUsername()));
    }

    @DeleteMapping("/delete")
    public Mono<BinaryResultVO> deleteChat(@RequestParam("userChatId") Long userChatId, @UserInfo UserPrincipal user) {
        return chatService.deleteChat(userChatId, Long.parseLong(user.getUsername()));
    }
}
