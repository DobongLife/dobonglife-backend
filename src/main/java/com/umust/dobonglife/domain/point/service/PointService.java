package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.point.controller.dto.response.MyPointsResponse;
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
        Long amount = pointRepository.sumAmountByUserId(userId);
        return (amount != null) ? amount : 0L;
    }

    private boolean isValid(Long point) {
        return point != null && point >= 0;
    }

    public Long getTotalEarnedPoints(Long userId) {
        return pointRepository.sumPositiveAmountByUserId(userId);
    }

    public List<PointHistoryDomainDto> getRecentHistories(Long userId, int limit) {
        List<Point> points = pointRepository.findTopNByUserId(userId, PageRequest.of(0, limit));

        return points.stream()
                .map(point -> new PointHistoryDomainDto(
                        point.getTitle(),
                        point.getAmount(),
                        point.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public MyPointsResponse getPointList(Long userId, int size, Long lastId, String order) {
        Long userTotalPoint = userService.getUserTotalPoint(userId);
        SliceResponse<PointResponse> pointsByCursor = getPointResponse(userId, size, lastId, order);

        return new MyPointsResponse(userTotalPoint, pointsByCursor);
    }

    public SliceResponse<PointResponse> getPointResponse(Long userId, int size, Long lastId, String order) {
        SortOrder parsedOrder = SortOrder.from(order);
        SliceResponse<PointResponse> pointsByCursor = pointRepository.findPointsByCursor(userId, size, lastId, parsedOrder);
        return pointsByCursor;
    }

    @Transactional
    public void usePoint(Long userId, Long pointId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (point.isUsed()) {
            throw new BusinessException(ErrorCode.POINT_ALREADY_USED);
        }

        long balanceUpdated = userRepository.decreaseBalance(userId, point.getAmount());
        if (balanceUpdated == 0) {
            throw new BusinessException(ErrorCode.POINT_CANNOT_NEGATIVE);
        }

        long afterBalance = userRepository.findBalanceById(userId);
        int pointBalanceUpdated = pointRepository.markUsedWithAfterBalance(userId, pointId, afterBalance);
        if (pointBalanceUpdated == 0) {
            throw new BusinessException(ErrorCode.POINT_ALREADY_USED);
        }
    }

    @Transactional
    public void earnPoint(User user, String title, Long amount) {
        Point point = Point.builder()
                .user(user)
                .title(title)
                .amount(amount)
                .isUsed(false)
                .build();

        pointRepository.save(point);
    }

    @Transactional
    public void usePoint(String title, Long point, User user) {
        Point newPoint = Point.builder()
                .user(user)
                .amount(-point)
                .isUsed(true)
                .title(title).build();

        pointRepository.save(newPoint);
    }
}
