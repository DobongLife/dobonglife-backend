package com.umust.dobonglife.domain.point.domain.repository.custom;

import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.global.common.response.slice.Cursor;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.common.response.slice.SortOrder;

import java.util.List;

public interface PointRepositoryCustom {
    SliceResponse<PointResponse> findPointsByCursor(Long userId, int size, Long lastId, SortOrder order);
}
