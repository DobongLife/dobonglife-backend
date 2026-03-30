package com.umust.dobonglife.domain.like.application.port.out;

import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.global.common.constant.TargetType;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.Set;

public interface LoadLikePort {

    Optional<Like> findByUserAndTarget(Long userId, TargetType targetType, Long targetId);

    boolean existsByUserAndTarget(Long userId, TargetType targetType, Long targetId);

    Set<Long> findTargetIdsByUserAndTargetType(Long userId, TargetType targetType);

    Slice<MyLikedPlaceResponse> findMyLikedPlaces(Long userId, Long lastId, int size);

    Slice<MyLikedCourseResponse> findMyLikedCourses(Long userId, Long lastId, int size);
}
