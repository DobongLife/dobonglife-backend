package com.umust.dobonglife.domain.like.application;

import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.domain.like.application.port.in.LikeCleanupUseCase;
import com.umust.dobonglife.domain.like.application.port.in.LikeRestoreUseCase;
import com.umust.dobonglife.domain.like.application.port.out.LoadLikePort;
import com.umust.dobonglife.domain.like.application.port.out.SaveLikePort;
import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService implements LikeCleanupUseCase, LikeRestoreUseCase {
    private final LoadLikePort loadLikePort;
    private final SaveLikePort saveLikePort;

    public boolean toggleLike(Long userId, TargetType targetType, Long targetId) {
        return loadLikePort.findByUserAndTarget(userId, targetType, targetId)
                .map(like -> {
                    saveLikePort.delete(like);
                    return false;
                })
                .orElseGet(() -> {
                    try {
                        saveLikePort.save(Like.of(userId, targetType, targetId));
                        return true;
                    } catch (DataIntegrityViolationException e) {
                        return true;
                    }
                });
    }

    @Transactional(readOnly = true)
    public CursorResponse<MyLikedPlaceResponse> getMyLikedPlaces(Long userId, Long lastId, int size) {
        Slice<MyLikedPlaceResponse> result = loadLikePort.findMyLikedPlaces(userId, lastId, size);
        return CursorUtils.toCursorResponse(result, response -> response);
    }

    @Transactional(readOnly = true)
    public CursorResponse<MyLikedCourseResponse> getMyLikedCourses(Long userId, Long lastId, int size) {
        Slice<MyLikedCourseResponse> result = loadLikePort.findMyLikedCourses(userId, lastId, size);
        return CursorUtils.toCursorResponse(result, response -> response);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, TargetType targetType, Long targetId) {
        return loadLikePort.existsByUserAndTarget(userId, targetType, targetId);
    }

    @Transactional(readOnly = true)
    public Set<Long> getLikedTargetIds(Long userId, TargetType targetType) {
        return loadLikePort.findTargetIdsByUserAndTargetType(userId, targetType);
    }

    @Override
    public void markPendingByUserId(Long userId) {
        saveLikePort.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
    }

    @Override
    public void finalizeByUserId(Long userId) {
        saveLikePort.deleteByUserIdAndStatus(userId, BaseStatus.PENDING);
    }

    @Override
    public void restoreByUserId(Long userId) {
        saveLikePort.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }
}
