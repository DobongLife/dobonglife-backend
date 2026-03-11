package com.umust.dobonglife.domain.like.application;

import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.domain.like.domain.repository.LikeRepository;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
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
}
