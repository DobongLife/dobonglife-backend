package com.umust.dobonglife.domain.auth.application;

import com.umust.dobonglife.domain.auth.model.dto.CustomOAuth2User;
import com.umust.dobonglife.domain.auth.model.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

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

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        // 기존 Auth 존재 여부 확인
        Auth auth = authRepository.findByProviderId(providerId)
                .orElseGet(() -> createAuth(oAuth2Response, provider, providerId));

        return new CustomOAuth2User(AuthDto.from(auth.getMember(),auth), oAuth2User.getAuthorities());
//        return new CustomOAuth2User(AuthDto.from(auth.getMember(), auth));
    }

    private Auth createAuth(OAuth2Response oAuth2Response, String provider, String providerId) {

        // 새 Member 생성
        Member member = Member.of(oAuth2Response.getEmail(), oAuth2Response.getName(), Role.MEMBER);
        memberRepository.save(member);

        // Auth 생성 및 저장
        Auth auth = Auth.builder()
                .provider(provider)
                .providerId(providerId)
                .member(member)
                .status(Status.ACTIVE)
                .build();

        return authRepository.save(auth);
    }
}
