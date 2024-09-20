package capstone.capbackend.dto;

import capstone.capbackend.dto.interfaces.OauthUserinfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString
public class KakaoUserInfoResponseDTO implements OauthUserinfoDTO {

    @Override
    public String getUserId() {
        return this.getKakaoAccount().getProfile().getNickname();
    }

    @Override
    public String getGender() {
        KakaoUserInfoKakaoAccount kakaoAccount = getKakaoAccount();
        if (kakaoAccount.hasGender && !kakaoAccount.genderNeedsAgreement) return kakaoAccount.getGender();
        else return "";
    }

    @Override
    public String getProfileImg() {
        KakaoUserInfoKakaoAccount kakaoAccount = getKakaoAccount();
        if (!kakaoAccount.profileImageNeedsAgreement && kakaoAccount.getProfile() != null && !kakaoAccount.getProfile().isDefaultImage) return kakaoAccount.getProfile().getProfileImageUrl();
        else return "";
    }

    @Override
    public String getSub() {
        return this.sub;
    }

    @JsonProperty("id")
    private long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("properties")
    private KakaoUserInfoProperties properties;

    @JsonProperty("kakao_account")
    private KakaoUserInfoKakaoAccount kakaoAccount;

    private String sub; // setter로 값을 넣어줘야함

    @Getter
    @ToString
    public static class KakaoUserInfoProperties {
        @JsonProperty("nickname") private String nickname;
        @JsonProperty("profile_image") private String profileImage;
        @JsonProperty("thumbnail_image") private String thumbnailImage;
    }

    @Getter
    @ToString
    public static class KakaoUserInfoKakaoAccount {
        @JsonProperty("profile_nickname_needs_agreement") private boolean profileNicknameNeedsAgreement;
        @JsonProperty("profile_image_needs_agreement") private boolean profileImageNeedsAgreement;
        @JsonProperty("profile") private KakaoUserInfoProfile profile;
        @JsonProperty("has_email") private boolean hasEmail;
        @JsonProperty("email_needs_agreement") private boolean emailNeedsAgreement;
        @JsonProperty("is_email_valid") private boolean isEmailValid;
        @JsonProperty("is_email_verified") private boolean isEmailVerified;
        @JsonProperty("email") private String email;
        @JsonProperty("has_birthday") private boolean hasBirthday;
        @JsonProperty("birthday_needs_agreement") private boolean birthdayNeedsAgreement;
        @JsonProperty("birthday") private String birthday; // MMdd
        @JsonProperty("birthday_type") private String birthdayType;
        @JsonProperty("has_gender") private boolean hasGender;
        @JsonProperty("gender_needs_agreement") private boolean genderNeedsAgreement;
        @JsonProperty("gender") private String gender;
    }

    @Getter
    @ToString
    public static class KakaoUserInfoProfile {
        @JsonProperty("nickname") private String nickname;
        @JsonProperty("thumbnail_image_url") private String thumbnailImageUrl;
        @JsonProperty("profile_image_url") private String profileImageUrl;
        @JsonProperty("is_default_image") private boolean isDefaultImage;
    }

}
