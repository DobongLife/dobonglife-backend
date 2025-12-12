package com.umust.dobonglife.domain.point.controller.dto.response;

import com.umust.dobonglife.domain.point.domain.constant.PointType;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PointResponse {

    private Long pointId;

    private String reason;

    private int amount;

    private PointType pointType;

    public static PointResponse from(Point point) {
        return PointResponse.builder()
                .pointId(point.getId())
                .reason(point.getReason())
                .amount(point.getAmount())
                .pointType(point.getPointType())
                .build();
    }
}
