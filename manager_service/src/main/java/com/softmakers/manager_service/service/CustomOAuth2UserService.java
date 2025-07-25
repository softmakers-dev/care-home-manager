package com.softmakers.manager_service.service;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.manager_service.dto.OAuth2UserInfo;
import com.softmakers.manager_service.dto.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private UserService userService;
    private final ServiceLifecycle serviceLifecycle;

    public CustomOAuth2UserService(ServiceLifecycle serviceLifecycle) {
        this.serviceLifecycle = serviceLifecycle;
        this.userService = this.serviceLifecycle.requestUserService();
    }

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> oAuth2UserAttributes = super.loadUser( userRequest ).getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of( registrationId, oAuth2UserAttributes );
        User user = getOrSave( oAuth2UserInfo );

        return new PrincipalDetails( user, oAuth2UserAttributes, userNameAttributeName );
    }

    private User getOrSave( OAuth2UserInfo oAuth2UserInfo ) {
        User user = this.userService.findUserByUsername( oAuth2UserInfo.name() );
        if( user == null ) {
            user = new User();
        }

        user.setUserName( oAuth2UserInfo.name() );
        user.setEmail( oAuth2UserInfo.email() != null ? oAuth2UserInfo.email() : "softmakers.dev@gmail.com" );
        user.setPassword( "passPass" );
        this.userService.addUser(user);

        User newUser = this.userService.findUserByUsername( oAuth2UserInfo.name() );
        return newUser;
    }
}
