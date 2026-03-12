package com.umust.dobonglife.domain.like.application;

import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.domain.like.domain.repository.LikeRepository;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;

    public boolean toggleLike(Long userId, TargetType targetType, Long targetId) {
        return likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .map(like -> {
                    likeRepository.delete(like);
                    return false;
                })
                .orElseGet(() -> {
                    likeRepository.save(Like.of(userId, targetType, targetId));
                    return true;
                });
    }

    @Transactional(readOnly = true)
    public CursorResponse<MyLikedPlaceResponse> getMyLikedPlaces(Long userId, Long lastId, int size) {
        Slice<MyLikedPlaceResponse> result = likeRepository.findMyLikedPlaces(userId, lastId, size);
        return CursorUtils.toCursorResponse(result, response -> response);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, TargetType targetType, Long targetId) {
        return likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }
}
