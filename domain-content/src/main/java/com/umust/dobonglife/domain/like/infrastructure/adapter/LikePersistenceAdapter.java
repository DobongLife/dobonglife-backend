package com.umust.dobonglife.domain.like.infrastructure.adapter;

import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.domain.like.application.port.out.LoadLikePort;
import com.umust.dobonglife.domain.like.application.port.out.SaveLikePort;
import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.domain.like.infrastructure.jpa.LikeRepository;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikePersistenceAdapter implements LoadLikePort, SaveLikePort {

    private final LikeRepository likeJpaRepository;

    // ── LoadLikePort ──

    @Override
    public Optional<Like> findByUserAndTarget(Long userId, TargetType targetType, Long targetId) {
        return likeJpaRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    @Override
    public boolean existsByUserAndTarget(Long userId, TargetType targetType, Long targetId) {
        return likeJpaRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    @Override
    public Set<Long> findTargetIdsByUserAndTargetType(Long userId, TargetType targetType) {
        return likeJpaRepository.findTargetIdsByUserIdAndTargetType(userId, targetType);
    }

    @Override
    public Slice<MyLikedPlaceResponse> findMyLikedPlaces(Long userId, Long lastId, int size) {
        return likeJpaRepository.findMyLikedPlaces(userId, lastId, size);
    }

    @Override
    public Slice<MyLikedCourseResponse> findMyLikedCourses(Long userId, Long lastId, int size) {
        return likeJpaRepository.findMyLikedCourses(userId, lastId, size);
    }

    // ── SaveLikePort ──

    @Override
    public Like save(Like like) {
        return likeJpaRepository.save(like);
    }

    @Override
    public void delete(Like like) {
        likeJpaRepository.delete(like);
    }
}
