package capstone.capbackend.controller;

import capstone.capbackend.config.security.UserInfo;
import capstone.capbackend.config.security.UserPrincipal;
import capstone.capbackend.entity.User;
import capstone.capbackend.service.UserService;
import capstone.capbackend.vo.BinaryResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Mono<User> getMe(@UserInfo UserPrincipal userPrincipal) {
        return userService.getMe(Long.parseLong(userPrincipal.getUsername()));
    }

    @DeleteMapping("/me")
    public Mono<BinaryResultVO> deleteAccount(@UserInfo UserPrincipal userPrincipal) {
        return userService.deleteAccount(Long.parseLong(userPrincipal.getUsername()));
    }
}
