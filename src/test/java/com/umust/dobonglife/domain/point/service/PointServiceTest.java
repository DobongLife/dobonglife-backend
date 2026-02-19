package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.point.controller.dto.response.MyPointsResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointGuideResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.common.response.slice.SortOrder;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @Mock
    PointRepository pointRepository;

    @InjectMocks
    PointService pointService;

    private User createUser(Long id, long balance) {
        return User.builder()
                .id(id)
                .email("test@example.com")
                .name("테스트")
                .role(Role.MEMBER)
                .provider(Provider.LOCAL)
                .balance(balance)
                .build();
    }

    @Nested
    @DisplayName("processUserPoint")
    class ProcessUserPoint {

        @Test
        @DisplayName("잔액이 충분하면 true를 반환한다")
        void 잔액이_충분하면_true를_반환한다() {
            User user = createUser(1L, 100L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            boolean result = pointService.processUserPoint(1L, 50L);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("잔액과 동일하면 true를 반환한다")
        void 잔액과_동일하면_true를_반환한다() {
            User user = createUser(1L, 100L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            boolean result = pointService.processUserPoint(1L, 100L);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("잔액이 부족하면 false를 반환한다")
        void 잔액이_부족하면_false를_반환한다() {
            User user = createUser(1L, 30L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            boolean result = pointService.processUserPoint(1L, 50L);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("사용자가 없으면 예외를 던진다")
        void 사용자가_없으면_예외를_던진다() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.processUserPoint(1L, 50L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getUserPoint")
    class GetUserPoint {

        @Test
        @DisplayName("사용자 잔액을 반환한다")
        void 사용자_잔액을_반환한다() {
            User user = createUser(1L, 500L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            Long result = pointService.getUserPoint(1L);

            assertThat(result).isEqualTo(500L);
        }

        @Test
        @DisplayName("사용자가 없으면 예외를 던진다")
        void 사용자가_없으면_예외를_던진다() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.getUserPoint(1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getPointList")
    class GetPointList {

        @Test
        @DisplayName("총 포인트와 가이드 내역을 함께 반환한다")
        void 총_포인트_가이드_내역을_함께_반환한다() {
            when(userService.getUserTotalPoint(1L)).thenReturn(500L);
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(20)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            when(pointRepository.findPointsByCursor(eq(1L), eq(20), isNull(), eq(SortOrder.DESC)))
                    .thenReturn(slice);

            MyPointsResponse result = pointService.getPointList(1L, 20, null, "DESC");

            assertThat(result.totalPoint()).isEqualTo(500L);
            assertThat(result.pointGuides()).hasSize(2);
            assertThat(result.pointList()).isEqualTo(slice);
        }

        @Test
        @DisplayName("포인트 가이드가 올바른 값을 갖는다")
        void 포인트_가이드가_올바른_값을_갖는다() {
            when(userService.getUserTotalPoint(1L)).thenReturn(0L);
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(20)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            when(pointRepository.findPointsByCursor(eq(1L), eq(20), isNull(), eq(SortOrder.DESC)))
                    .thenReturn(slice);

            MyPointsResponse result = pointService.getPointList(1L, 20, null, "DESC");

            List<PointGuideResponse> guides = result.pointGuides();
            assertThat(guides.get(0).title()).isEqualTo("후기 작성");
            assertThat(guides.get(0).rewardPoint()).isEqualTo(10L);
            assertThat(guides.get(1).title()).isEqualTo("코스 등록");
            assertThat(guides.get(1).rewardPoint()).isEqualTo(30L);
        }

        @Test
        @DisplayName("정렬 순서를 레포지토리에 전달한다")
        void 정렬_순서를_레포지토리에_전달한다() {
            when(userService.getUserTotalPoint(1L)).thenReturn(0L);
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(20)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            when(pointRepository.findPointsByCursor(eq(1L), eq(20), isNull(), eq(SortOrder.ASC)))
                    .thenReturn(slice);

            pointService.getPointList(1L, 20, null, "ASC");

            verify(pointRepository).findPointsByCursor(eq(1L), eq(20), isNull(), eq(SortOrder.ASC));
        }

        @Test
        @DisplayName("lastId를 레포지토리에 전달한다")
        void lastId를_레포지토리에_전달한다() {
            when(userService.getUserTotalPoint(1L)).thenReturn(0L);
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(20)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            when(pointRepository.findPointsByCursor(eq(1L), eq(20), eq(5L), eq(SortOrder.DESC)))
                    .thenReturn(slice);

            pointService.getPointList(1L, 20, 5L, "DESC");

            verify(pointRepository).findPointsByCursor(eq(1L), eq(20), eq(5L), eq(SortOrder.DESC));
        }
    }

    @Nested
    @DisplayName("earnPoint")
    class EarnPoint {

        @Test
        @DisplayName("포인트 적립 시 잔액이 증가하고 엔티티가 저장된다")
        void 포인트_적립_시_잔액이_증가하고_엔티티가_저장된다() {
            User user = createUser(1L, 100L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            pointService.earnPoint(1L, "후기 작성", 10L);

            assertThat(user.getBalance()).isEqualTo(110L);
            ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
            verify(pointRepository).save(captor.capture());
            Point saved = captor.getValue();
            assertThat(saved.getAmount()).isEqualTo(10L);
            assertThat(saved.getAfterBalance()).isEqualTo(110L);
            assertThat(saved.isUsed()).isFalse();
        }

        @Test
        @DisplayName("사용자가 없으면 예외를 던진다")
        void 사용자가_없으면_예외를_던진다() {
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.earnPoint(1L, "후기 작성", 10L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("음수 금액이면 예외를 던진다")
        void 음수_금액이면_예외를_던진다() {
            User user = createUser(1L, 100L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> pointService.earnPoint(1L, "후기 작성", -10L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POINT_CANNOT_NEGATIVE);
        }

        @Test
        @DisplayName("올바른 afterBalance를 저장한다")
        void 올바른_afterBalance를_저장한다() {
            User user = createUser(1L, 200L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            pointService.earnPoint(1L, "코스 등록", 30L);

            ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
            verify(pointRepository).save(captor.capture());
            assertThat(captor.getValue().getAfterBalance()).isEqualTo(230L);
        }
    }

    @Nested
    @DisplayName("usePoint")
    class UsePoint {

        @Test
        @DisplayName("포인트 사용 시 잔액이 차감되고 엔티티가 저장된다")
        void 포인트_사용_시_잔액이_차감되고_엔티티가_저장된다() {
            User user = createUser(1L, 100L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            pointService.usePoint(1L, "쿠폰 교환", 30L);

            assertThat(user.getBalance()).isEqualTo(70L);
            ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
            verify(pointRepository).save(captor.capture());
            Point saved = captor.getValue();
            assertThat(saved.getAmount()).isEqualTo(-30L);
            assertThat(saved.getAfterBalance()).isEqualTo(70L);
            assertThat(saved.isUsed()).isTrue();
        }

        @Test
        @DisplayName("사용자가 없으면 예외를 던진다")
        void 사용자가_없으면_예외를_던진다() {
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.usePoint(1L, "쿠폰 교환", 30L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("잔액이 부족하면 예외를 던진다")
        void 잔액이_부족하면_예외를_던진다() {
            User user = createUser(1L, 20L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> pointService.usePoint(1L, "쿠폰 교환", 50L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_POINT);
        }

        @Test
        @DisplayName("음수 금액이면 예외를 던진다")
        void 음수_금액이면_예외를_던진다() {
            User user = createUser(1L, 100L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> pointService.usePoint(1L, "쿠폰 교환", -30L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POINT_CANNOT_NEGATIVE);
        }

        @Test
        @DisplayName("음수 amount로 저장한다")
        void 음수_amount로_저장한다() {
            User user = createUser(1L, 100L);
            when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

            pointService.usePoint(1L, "쿠폰 교환", 30L);

            ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
            verify(pointRepository).save(captor.capture());
            assertThat(captor.getValue().getAmount()).isEqualTo(-30L);
        }
    }

    @Nested
    @DisplayName("deleteByUserId")
    class DeleteByUserId {

        @Test
        @DisplayName("레포지토리 삭제를 호출한다")
        void 레포지토리_삭제를_호출한다() {
            pointService.deleteByUserId(1L);

            verify(pointRepository, times(1)).deleteByUserId(1L);
        }
    }

    @Nested
    @DisplayName("Point Entity")
    class PointEntity {

        @Test
        @DisplayName("markUsed 성공")
        void markUsed_성공() {
            Point point = Point.builder()
                    .id(1L)
                    .amount(10L)
                    .title("후기 작성")
                    .afterBalance(110L)
                    .isUsed(false)
                    .build();

            point.markUsed();

            assertThat(point.isUsed()).isTrue();
        }

        @Test
        @DisplayName("markUsed 이미 사용된 경우 예외")
        void markUsed_이미_사용된_경우_예외() {
            Point point = Point.builder()
                    .id(1L)
                    .amount(10L)
                    .title("후기 작성")
                    .afterBalance(110L)
                    .isUsed(true)
                    .build();

            assertThatThrownBy(point::markUsed)
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POINT_ALREADY_USED);
        }
    }
}
