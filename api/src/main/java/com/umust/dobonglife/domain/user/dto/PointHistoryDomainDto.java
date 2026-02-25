package com.umust.dobonglife.domain.user.dto;

import com.umust.dobonglife.domain.point.domain.entity.Point;

import java.time.LocalDateTime;

public record PointHistoryDomainDto(
        String title,
        Long amount,
        LocalDateTime createdAt
) {
    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static PointHistoryDomainDto from(Point point) {
        return new PointHistoryDomainDto(
                point.getTitle(),
                point.getAmount(),
                point.getCreatedAt()
        );
    }
}
