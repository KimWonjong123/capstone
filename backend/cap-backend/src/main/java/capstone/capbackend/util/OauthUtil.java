package capstone.capbackend.util;

import capstone.capbackend.dto.OauthPublicKeysDTO;
import capstone.capbackend.dto.OauthTokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class OauthUtil {

    private final WebClient webClient;

    private static final ConcurrentHashMap<String, OauthPublicKeysDTO> publicKeyCache = new ConcurrentHashMap<String, OauthPublicKeysDTO>();

    public Mono<OauthPublicKeysDTO.Key> getPublicKey(String provider, String url, String uri, OauthTokenDTO token) {
        String header = new String(Base64.getDecoder().decode(token.getIdToken().split("\\.")[0]));
        String kid = header.split("\"kid\":\"")[1].split("\"")[0];
        return Mono.defer(() -> {
                    if (!publicKeyCache.containsKey(provider)) {
                        return putPublicKeys(provider, url, uri);
                    }
                    return Mono.just(publicKeyCache.get(provider));
                })
                .map(OauthPublicKeysDTO::getKeys)
                .flatMapMany(Flux::fromIterable)
                .filter(key -> key.getKid().equals(kid))
                .switchIfEmpty(updatePublicKeys(provider, url, uri)
                        .map(OauthPublicKeysDTO::getKeys)
                        .flatMapMany(Flux::fromIterable)
                        .filter(key -> key.getKid().equals(kid))
                )
                .next();
    }

    private Mono<OauthPublicKeysDTO> putPublicKeys(String provider, String url, String uri) {
        return webClient.mutate()
                .baseUrl(url)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                                        clientResponse -> clientResponse.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(OauthPublicKeysDTO.class)
                .doOnNext(keys -> publicKeyCache.putIfAbsent(provider, keys))
                .log();
    }


    private Mono<OauthPublicKeysDTO> updatePublicKeys(String provider, String url, String uri) {
        return webClient.mutate()
                .baseUrl(url)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                                        clientResponse -> clientResponse.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(OauthPublicKeysDTO.class)
                .doOnNext(keys -> publicKeyCache.replace(provider, keys))
                .log();
    }

    public Claims parseClaims(String idToken, OauthPublicKeysDTO.Key key) {
        try {
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE().getBytes());
            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN().getBytes());
            RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));
            PublicKey publicKey = KeyFactory.getInstance(key.getKty()).generatePublic(spec);
            return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(idToken).getBody();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
