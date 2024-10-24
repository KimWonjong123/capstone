package capstone.capbackend.controller;

import capstone.capbackend.config.security.UserInfo;
import capstone.capbackend.config.security.UserPrincipal;
import capstone.capbackend.dto.CreateChatRequestDTO;
import capstone.capbackend.dto.CreateChatResponseDTO;
import capstone.capbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
