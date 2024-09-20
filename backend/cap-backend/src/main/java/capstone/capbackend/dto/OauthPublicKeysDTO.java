package capstone.capbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OauthPublicKeysDTO {
    List<Key> keys;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Key {
        @JsonProperty("kty") String kty;
        @JsonProperty("kid") String kid;
        @JsonProperty("use") String sig;
        @JsonProperty("alg") String alg;
        @JsonProperty("n") String n;
        @JsonProperty("e") String e;
    }
}
