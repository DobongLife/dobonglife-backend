package com.umust.dobonglife.domain.auth.infrastructure.security;

import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.domain.auth.domain.AuthUserInfo;
import com.umust.dobonglife.domain.auth.dto.response.GoogleResponse;
import com.umust.dobonglife.domain.auth.dto.response.KakaoResponse;
import com.umust.dobonglife.domain.auth.dto.response.NaverResponse;
import com.umust.dobonglife.domain.auth.dto.response.OAuth2Response;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthUserPort authUserPort;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;
        log.info("oAuth2User.getAttributes() : {}", oAuth2User.getAttributes());
        switch (registrationId) {
            case "naver" -> oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            case "kakao" -> oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            case "google" -> oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            default -> {
                return null;
            }
        }

        Provider provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProvider().getValue() + "_" + oAuth2Response.getProviderId();

        AuthUserInfo userInfo = authUserPort.findOrCreateOAuthUser(
                provider, providerId, oAuth2Response.getEmail(), oAuth2Response.getName());

        return UserPrincipal.builder()
                .userId(userInfo.userId())
                .userName(userInfo.name())
                .role(userInfo.role())
                .provider(provider)
                .authorities(List.of(userInfo.role().toAuthority()))
                .build();
    }
}
