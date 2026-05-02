package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.common.constant.TargetType;

import java.util.Map;

public interface LikePort {
    boolean toggleLike(Long userId, TargetType targetType, Long targetId);
    Map<String, Object> getMyLikedPlaces(Long userId, Long lastId, int size);
    Map<String, Object> getMyLikedCourses(Long userId, Long lastId, int size);
}
