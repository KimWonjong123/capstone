package capstone.capbackend.config.security;

import io.jsonwebtoken.Claims;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
public class UserAuthToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final Map<String, Object> claims;
    private static final String CLAIM_KEY_ROLE = "ROLE";

    @Override
    public Object getCredentials() {
        return this.claims;
    }

    @Override
    public Object getPrincipal() {
        return new UserPrincipal(this.getAuthorities(), this.userId.toString());
    }

    @Override
    public boolean isAuthenticated() { return true; }

    public UserAuthToken(Claims claims) {
        super(List.of(new SimpleGrantedAuthority("ROLE")));
        this.userId = Long.parseLong(claims.getSubject());
        this.claims = new HashMap<>(claims);
    }

}
