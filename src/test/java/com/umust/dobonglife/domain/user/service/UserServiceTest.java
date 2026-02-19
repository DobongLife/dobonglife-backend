package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.user.controller.dto.request.PasswordUpdateRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .name("테스트유저")
                .password("encodedPassword")
                .provider(Provider.LOCAL)
                .role(Role.MEMBER)
                .balance(1000L)
                .build();
    }

    @Test
    @DisplayName("signUp - 정상 회원가입")
    void signUp_성공() {
        // given
        SignupRequest request = new SignupRequest("new@example.com", "새유저", "password123");
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);
        given(mailService.getStoredSignUpCode("new@example.com")).willReturn("VERIFIED");
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(createTestUser());

        // when
        userService.signUp(request);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("signUp - 중복 이메일이면 USER_DUPLICATE_EMAIL 예외")
    void signUp_중복이메일_예외() {
        // given
        SignupRequest request = new SignupRequest("dup@example.com", "유저", "password");
        given(userRepository.existsByEmail("dup@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("signUp - 이메일 미인증이면 AUTHCODE_UNAUTHORIZED 예외")
    void signUp_미인증_예외() {
        // given
        SignupRequest request = new SignupRequest("new@example.com", "유저", "password");
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);
        given(mailService.getStoredSignUpCode("new@example.com")).willReturn("123456");

        // when & then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.AUTHCODE_UNAUTHORIZED);
    }

    @Test
    @DisplayName("deleteAccount - 정상 계정 삭제")
    void deleteAccount_성공() {
        // given
        User user = createTestUser();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userService.deleteAccount(1L);

        // then
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteAccount - 유저 없으면 USER_NOT_FOUND 예외")
    void deleteAccount_유저없음_예외() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteAccount(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("updateMyPassword - 정상 비밀번호 변경")
    void updateMyPassword_성공() {
        // given
        User user = createTestUser();
        PasswordUpdateRequest request = new PasswordUpdateRequest("test@example.com", "123456", "newPassword");
        given(userRepository.findByEmailAndProvider("test@example.com", Provider.LOCAL))
                .willReturn(Optional.of(user));
        given(passwordEncoder.encode("newPassword")).willReturn("newEncodedPassword");

        // when
        userService.updateMyPassword(request);

        // then
        assertThat(user.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    @DisplayName("updateMyPassword - 유저 없으면 USER_NOT_FOUND 예외")
    void updateMyPassword_유저없음_예외() {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("notfound@example.com", "123456", "newPassword");
        given(userRepository.findByEmailAndProvider("notfound@example.com", Provider.LOCAL))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateMyPassword(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("updateMyPassword - 소셜 유저(password null)이면 USER_IS_SOCIAL_LOGGED 예외")
    void updateMyPassword_소셜유저_예외() {
        // given
        User socialUser = User.builder()
                .id(2L)
                .email("social@example.com")
                .name("소셜유저")
                .password(null)
                .provider(Provider.LOCAL)
                .role(Role.MEMBER)
                .build();
        PasswordUpdateRequest request = new PasswordUpdateRequest("social@example.com", "123456", "newPassword");
        given(userRepository.findByEmailAndProvider("social@example.com", Provider.LOCAL))
                .willReturn(Optional.of(socialUser));

        // when & then
        assertThatThrownBy(() -> userService.updateMyPassword(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_IS_SOCIAL_LOGGED);
    }

    @Test
    @DisplayName("findOrCreateOAuthUser - 기존 유저 존재 시 반환")
    void findOrCreateOAuthUser_기존유저() {
        // given
        User existingUser = User.builder()
                .id(3L)
                .email("kakao@example.com")
                .name("카카오유저")
                .provider(Provider.KAKAO)
                .providerId("kakao123")
                .role(Role.MEMBER)
                .build();
        given(userRepository.findByProviderAndProviderId(Provider.KAKAO, "kakao123"))
                .willReturn(Optional.of(existingUser));

        // when
        User result = userService.findOrCreateOAuthUser(Provider.KAKAO, "kakao123", "kakao@example.com", "카카오유저");

        // then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getProvider()).isEqualTo(Provider.KAKAO);
    }

    @Test
    @DisplayName("findOrCreateOAuthUser - 신규 유저 생성")
    void findOrCreateOAuthUser_신규유저() {
        // given
        User newUser = User.builder()
                .id(4L)
                .email("google@example.com")
                .name("구글유저")
                .provider(Provider.GOOGLE)
                .providerId("google456")
                .role(Role.MEMBER)
                .build();
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "google456"))
                .willReturn(Optional.empty());
        given(userRepository.existsByEmail("google@example.com")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(newUser);

        // when
        User result = userService.findOrCreateOAuthUser(Provider.GOOGLE, "google456", "google@example.com", "구글유저");

        // then
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getProvider()).isEqualTo(Provider.GOOGLE);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getUserInfo - MyPageResponse 반환")
    void getUserInfo_성공() {
        // given
        User user = createTestUser();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        MyPageResponse response = userService.getUserInfo(1L);

        // then
        assertThat(response.name()).isEqualTo("테스트유저");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.role()).isEqualTo(Role.MEMBER);
    }

    @Test
    @DisplayName("getUserTotalPoint - 포인트 잔액 반환")
    void getUserTotalPoint_성공() {
        // given
        User user = createTestUser();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        Long balance = userService.getUserTotalPoint(1L);

        // then
        assertThat(balance).isEqualTo(1000L);
    }

    @Test
    @DisplayName("canExchangeCoupon - 차단 유저이면 COUPON_EXCHANGE_RESTRICTED 예외")
    void canExchangeCoupon_차단유저_예외() {
        // given
        User blockedUser = User.builder()
                .id(5L)
                .email("blocked@example.com")
                .name("차단유저")
                .password("encoded")
                .provider(Provider.LOCAL)
                .role(Role.MEMBER)
                .isBlocked(true)
                .blockedAt(LocalDateTime.now())
                .build();
        given(userRepository.findById(5L)).willReturn(Optional.of(blockedUser));

        // when & then
        assertThatThrownBy(() -> userService.canExchangeCoupon(5L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.COUPON_EXCHANGE_RESTRICTED);
    }

    @Test
    @DisplayName("handleDeletion - 삭제 카운트 증가")
    void handleDeletion_성공() {
        // given
        User user = createTestUser();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userService.handleDeletion(1L);

        // then
        assertThat(user.getDeleteCount()).isEqualTo(1);
        verify(userRepository).save(user);
    }
}
