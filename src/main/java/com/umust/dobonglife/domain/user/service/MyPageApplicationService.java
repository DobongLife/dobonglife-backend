package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.coupon.service.CouponService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.controller.dto.PointHistoryDomainDto;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPageApplicationService { // TODO: 이벤트 어떻게 하기로 했는지 확인

    private final PointService pointService;
    private final UserService userService;
    // private final EventService eventService;
    private final CouponService couponService;
    private final ReviewService reviewService;

    public MyPageResponse getMyPage(Long userId) {
        User user = userService.findById(userId);

        MyPageResponse.ActivitySummary summary = new MyPageResponse.ActivitySummary(
                //eventService.getParticipationCount(userId),
                0,
                couponService.getOwnedCouponCount(userId),
                reviewService.getWrittenReviewCount(userId),
                pointService.getTotalEarnedPoints(userId)
        );

        List<MyPageResponse.PointHistoryDto> recentHistories = pointService.getRecentHistories(userId, 3)
                .stream()
                .map(this::convertToPointHistoryDto)
                .toList();

        return new MyPageResponse(
                user.getName(),
                user.getEmail(),
                user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                summary,
                pointService.getUserPoint(userId),
                recentHistories
        );

    }

    private MyPageResponse.PointHistoryDto convertToPointHistoryDto(PointHistoryDomainDto history) {
        return new MyPageResponse.PointHistoryDto(
                history.title(),
                history.createdAt().format(DateTimeFormatter.ofPattern("yyyy. M. d.")),
                history.amount(),
                history.amount() > 0 ? "적립" : "사용"
        );
    }

    private String formatJoinDate(LocalDateTime createdAt) {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy년 M월"));
    }
}
