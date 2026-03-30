package com.umust.dobonglife.domain.like.application;

import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.domain.like.application.port.in.GetLikeUseCase;
import com.umust.dobonglife.domain.like.application.port.out.LoadLikePort;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeQueryService implements GetLikeUseCase {

    private final LoadLikePort loadLikePort;

    @Override
    public CursorResponse<MyLikedPlaceResponse> getMyLikedPlaces(Long userId, Long lastId, int size) {
        Slice<MyLikedPlaceResponse> result = loadLikePort.findMyLikedPlaces(userId, lastId, size);
        return CursorUtils.toCursorResponse(result, response -> response);
    }

    @Override
    public CursorResponse<MyLikedCourseResponse> getMyLikedCourses(Long userId, Long lastId, int size) {
        Slice<MyLikedCourseResponse> result = loadLikePort.findMyLikedCourses(userId, lastId, size);
        return CursorUtils.toCursorResponse(result, response -> response);
    }

    @Override
    public boolean isLiked(Long userId, TargetType targetType, Long targetId) {
        return loadLikePort.existsByUserAndTarget(userId, targetType, targetId);
    }

    @Override
    public Set<Long> getLikedTargetIds(Long userId, TargetType targetType) {
        return loadLikePort.findTargetIdsByUserAndTargetType(userId, targetType);
    }
}
