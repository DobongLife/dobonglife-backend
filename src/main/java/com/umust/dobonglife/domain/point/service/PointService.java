package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.point.domain.Point;
import com.umust.dobonglife.domain.point.infrastructure.repository.PointRepository;
import com.umust.dobonglife.domain.user.controller.dto.PointHistoryDomainDto;
import com.umust.dobonglife.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    public boolean processUserPoint(Long userId) {
        Long currentPoint = getUserPoint(userId);
        return isValid(currentPoint);
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

    @Transactional
    public void earnPoint(User user, String title, Long amount) {
        Point point = Point.builder()
                .user(user)
                .title(title)
                .amount(amount)
                .build();

        pointRepository.save(point);
    }
}
