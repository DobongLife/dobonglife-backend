package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.Map;

public interface LikePort {
    boolean toggleLike(Long userId, TargetType targetType, Long targetId);
    CursorResponse<Map<String, Object>> getMyLikedPlaces(Long userId, Long lastId, int size);
    CursorResponse<Map<String, Object>> getMyLikedCourses(Long userId, Long lastId, int size);
}
