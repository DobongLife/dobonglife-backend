package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.Map;

public interface ReviewPort {
    Map<String, Object> createReview(Long userId, Map<String, Object> request);
    Map<String, Object> updateReview(Long reviewId, Long userId, Map<String, Object> request);
    void deleteReview(Long reviewId, Long userId);
    CursorResponse<Map<String, Object>> getReviews(TargetType targetType, Long targetId, Long lastId, int size);
    CursorResponse<Map<String, Object>> getMyReviews(Long userId, TargetType targetType, Long lastId, int size);
}
