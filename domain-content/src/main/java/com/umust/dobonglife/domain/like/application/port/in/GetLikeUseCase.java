package com.umust.dobonglife.domain.like.application.port.in;

import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.Set;

public interface GetLikeUseCase {

    CursorResponse<MyLikedPlaceResponse> getMyLikedPlaces(Long userId, Long lastId, int size);

    CursorResponse<MyLikedCourseResponse> getMyLikedCourses(Long userId, Long lastId, int size);

    boolean isLiked(Long userId, TargetType targetType, Long targetId);

    Set<Long> getLikedTargetIds(Long userId, TargetType targetType);
}
