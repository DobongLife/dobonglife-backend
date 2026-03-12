package com.umust.dobonglife.domain.like.domain.repository;

import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.domain.like.domain.repository.custom.LikeRepositoryCustom;
import com.umust.dobonglife.global.common.constant.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {

    Optional<Like> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
}
