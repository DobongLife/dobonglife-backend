package com.umust.dobonglife.domain.point.domain.repository.custom;

import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.global.common.response.slice.Cursor;

import java.util.List;

public interface PointRepositoryCustom {
    List<Point> findMyPoint(Long userId, int size, Cursor cursor);
}
