package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.point.controller.dto.response.MyPointsResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointGuideResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.user.controller.dto.PointHistoryDomainDto;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.slice.Cursor;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.common.response.slice.SortOrder;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 포인트 계산 결과 필드를 User 엔티티에 두는 이유
 * 비록 포인트가 갱신될때마다 필드로 갱신해줘야 되서 쿼리가 1개 늘어나긴 하지만,
 * 대부분의 서비스는 쓰기보다 조회의 경우가 더 많다.
 * 따라서 포인트 내역이 매우 많을 때는 sum으로 집계하는 것보다
 * User 엔티티에서 그냥 칼럼 하나로 조회하는 게 더 효율적이다.
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PointRepository pointRepository;

    public boolean processUserPoint(Long userId, Long point) {
        Long currentPoint = getUserPoint(userId);
        return isValid(currentPoint - point);
    }

    public Long getUserPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user.getBalance();
    }

    private boolean isValid(Long point) {
        return point != null && point >= 0;
    }

    @Transactional(readOnly = true)
    public MyPointsResponse getPointList(Long userId, int size, Long lastId, String order) {
        Long userTotalPoint = userService.getUserTotalPoint(userId);
        SliceResponse<PointResponse> pointsByCursor = getPointResponse(userId, size, lastId, order);

        List<PointGuideResponse> guides = List.of( // TODO: 확장성 어떻게 고려할지 생각하기
                new PointGuideResponse("후기 작성", 10L),
                new PointGuideResponse("코스 등록", 30L)
        );

        return new MyPointsResponse(userTotalPoint, guides, pointsByCursor);
    }

    @Transactional(readOnly = true)
    public SliceResponse<PointResponse> getPointResponse(Long userId, int size, Long lastId, String order) {
        SortOrder parsedOrder = SortOrder.from(order);
        SliceResponse<PointResponse> pointsByCursor = pointRepository.findPointsByCursor(userId, size, lastId, parsedOrder);
        return pointsByCursor;
    }

    @Transactional
    public void earnPoint(Long userId, String title, Long amount) {
        // 비관적 락 걸고, balance 가산
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.earnPoint(amount);

        Point point = Point.builder()
                .user(user)
                .title(title)
                .amount(amount)
                .isUsed(false)
                .afterBalance(user.getBalance())
                .build();

        pointRepository.save(point);
    }

    @Transactional
    public void usePoint(Long userId, String title, Long pointAmount) {
        // 비관적 걸고, balance 차감
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.usePoint(pointAmount);

        Point newPoint = Point.builder()
                .user(user)
                .amount(-pointAmount)
                .isUsed(true)
                .title(title)
                .afterBalance(user.getBalance())
                .build();

        pointRepository.save(newPoint);
    }
}
