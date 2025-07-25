package com.softmakers.manager_service.dto;

import com.softmakers.error.exception.BusinessException;
import com.softmakers.manager_domain.entity.User;
import lombok.Builder;

import java.util.Map;

import static com.softmakers.error.ErrorCode.ILLEGAL_REGISTRATION_ID;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profile
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "github" -> ofGithub(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new BusinessException(ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGithub(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("avatar_url"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .build();
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .userName(name)
                .build();
    }

}
