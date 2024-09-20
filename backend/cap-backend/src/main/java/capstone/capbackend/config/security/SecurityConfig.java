package capstone.capbackend.config.security;

import capstone.capbackend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    @SuppressWarnings("removal")
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationWebFilter authFilter) throws Exception {
        return http
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling()
                .authenticationEntryPoint((ex, e) -> Mono.fromRunnable(() -> ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((ex, e) -> Mono.fromRunnable(() -> ex.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("auth/token/**").authenticated()
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated())
                .headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN)
                .and()
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll())
//                .headers(headers -> headers
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilterBefore(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();

    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationWebFilter authenticationWebFilter(Function<ServerWebExchange, Mono<Authentication>> serverAuthConverter) {
        ReactiveAuthenticationManager authenticationManager = Mono::just;
        var authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setAuthenticationConverter(serverAuthConverter);
        authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return authenticationWebFilter;
    }

    @Bean
    public Function<ServerWebExchange, Mono<Authentication>> serverAuthenticationConverter() {
        return new Function<ServerWebExchange, Mono<Authentication>>() {
            private static final String BEARER = "Bearer ";
            private static final int BEARER_LEN = BEARER.length();
            private static final Predicate<String> matchBearerLength = authVal -> authVal.length() > BEARER_LEN;
            private static final Function<String, Mono<String>> getAccessToken = authVal -> Mono.justOrEmpty(authVal.substring(BEARER_LEN));
            public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
                return Mono.justOrEmpty(serverWebExchange)
                        .flatMap(jwtUtil::extractToken)
                        .filter(matchBearerLength)
                        .flatMap(getAccessToken)
                        .flatMap(jwtUtil::parseClaims)
                        .flatMap(generateAuthentication());
            }
        };
    }

    private Function<Claims, Mono<Authentication>> generateAuthentication() {
        return claims -> Mono.justOrEmpty(new UserAuthToken(claims));
    }
}
