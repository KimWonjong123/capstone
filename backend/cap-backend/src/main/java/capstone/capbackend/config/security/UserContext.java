package capstone.capbackend.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserContext {

    public Mono<String> getUserInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.error(new RuntimeException("USER AUTHENTICATION NOT FOUND")))
                .map(Authentication::getPrincipal)
                .map(user -> ((UserDetails) user).getUsername())
                .switchIfEmpty(Mono.error(new RuntimeException("USER PRINCIPAL IS NOT FOUND")));
    }
}
