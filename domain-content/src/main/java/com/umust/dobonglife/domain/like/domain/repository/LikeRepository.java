package com.umust.dobonglife.domain.like.domain.repository;

import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.domain.like.domain.repository.custom.LikeRepositoryCustom;
import com.umust.dobonglife.global.common.constant.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {

    Optional<Like> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    @Query("SELECT l.targetId FROM Like l WHERE l.userId = :userId AND l.targetType = :targetType")
    Set<Long> findTargetIdsByUserIdAndTargetType(@Param("userId") Long userId, @Param("targetType") TargetType targetType);
}
