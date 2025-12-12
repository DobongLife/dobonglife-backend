package com.umust.dobonglife.domain.point.domain.repository.custom;

import com.umust.dobonglife.domain.point.domain.entity.Point;

import java.util.List;

public interface PointRepositoryCustom {
    List<Point> findMyPoint(Long userId);
}
