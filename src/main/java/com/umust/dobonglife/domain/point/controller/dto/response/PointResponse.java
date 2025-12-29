package com.umust.dobonglife.domain.point.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PointResponse {

    private Long pointId;

    private String reason;

    private int amount;

    @QueryProjection
    public PointResponse(
            Long pointId,
            String reason,
            int amount
    ) {
        this.pointId = pointId;
        this.reason = reason;
        this.amount = amount;
    }

    public static PointResponse from(Point point) {
        return PointResponse.builder()
                .pointId(point.getId())
                .reason(point.getReason())
                .amount(point.getAmount())
                .build();
    }
}
