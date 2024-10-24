package capstone.capbackend.controller;

import capstone.capbackend.config.security.UserInfo;
import capstone.capbackend.config.security.UserPrincipal;
import capstone.capbackend.dto.ChatInfoDTO;
import capstone.capbackend.dto.CreateChatRequestDTO;
import capstone.capbackend.dto.CreateChatResponseDTO;
import capstone.capbackend.dto.JoiningChatDTO;
import capstone.capbackend.entity.UserChat;
import capstone.capbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/chat")
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
    public Flux<JoiningChatDTO> getCreatedChats(@UserInfo UserPrincipal user) {
        return chatService.getCreatedChats(Long.parseLong(user.getUsername()));
    }

    @GetMapping("/list/created/joined")
    public Flux<ChatInfoDTO> getJoinedCreatedChats(@RequestParam("chatId") Long chatId, @UserInfo UserPrincipal user) {
        return chatService.getJoinedCreatedChats(chatId, Long.parseLong(user.getUsername()));
    }
}
