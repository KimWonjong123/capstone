package capstone.capbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoUserTokenResponseDTO extends OauthTokenDTO {
    @JsonProperty("token_type") private String tokenType;
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("id_token") private String idToken;
    @JsonProperty("expires_in") private int expiresIn;
    @JsonProperty("refresh_token") private String refreshToken;
    @JsonProperty("refresh_token_expires_in") private int refreshTokenExpiresIn;
    @JsonProperty("scope") private String scope;
}
