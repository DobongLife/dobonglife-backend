package com.umust.dobonglife.domain.point.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class PointResponse {

    private Long pointId;
    private String title;
    private Long amount;
    private LocalDateTime createAt;
    private long afterBalance;

    @QueryProjection
    public PointResponse(
            Long pointId,
            String reason,
            Long amount,
            LocalDateTime createAt,
            long afterBalance
    ) {
        this.pointId = pointId;
        this.title = reason;
        this.amount = amount;
        this.createAt = createAt;
        this.afterBalance = afterBalance;
    }

    public static PointResponse from(Point point) {
        return PointResponse.builder()
                .pointId(point.getId())
                .title(point.getTitle())
                .amount(point.getAmount())
                .build();
    }
}
